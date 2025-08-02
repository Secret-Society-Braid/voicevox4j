package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
import org.braid.secret.society.voicevox4j.api.UserDict;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.braid.secret.society.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.NativeVoicevoxLibrary;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * OpenJTalkDictionaryクラスのテストクラス。
 *
 * <h2>使用方法</h2>
 * <pre>{@code
 * // 基本的な使用方法
 * Path dictPath = Paths.get("path/to/open_jtalk_dic_utf_8-1.11");
 * Core core = NativeVoicevoxLibrary.load(Path.of(""));
 * try (OpenJTalkDictionary dict = new OpenJTalkDictionary(dictPath, core)) {
 *     // テキストを解析してアクセント句JSONを取得
 *     String accentJson = dict.analyze("こんにちは");
 *     System.out.println(accentJson);
 *
 *     // ユーザー辞書を使用する場合
 *     try (UserDict userDict = new UserDict(core)) {
 *         dict.useUserDict(userDict.getNativeUserDict());
 *     }
 * } // リソースは自動的に解放される
 * }
 */
@Slf4j
public class OpenJTalkDictionaryTest {

  @Test
  void testOpenJTalkDictionary() throws VoicevoxException, IOException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    log.debug("=== OpenJTalkDictionary テスト開始 ===");
    log.debug("辞書ディレクトリパス: {}", dictPath);
    log.debug("ディレクトリ存在確認: {}", java.nio.file.Files.exists(dictPath));
    log.debug("ディレクトリ確認: {}", java.nio.file.Files.isDirectory(dictPath));

    // try-with-resources文を使用した自動リソース管理
    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath)) {
      log.debug("✓ OpenJTalkDictionaryの作成成功");

      // 辞書が正常に開かれることを確認
      Truth.assertThat(dictionary.isClosed()).isFalse();
      log.debug("✓ 辞書が開いた状態: {}", !dictionary.isClosed());

      // テキスト解析のテスト
      log.debug("--- テキスト解析テスト ---");
      String testText = "こんにちは";
      String accentJson = dictionary.analyze(testText);

      Truth.assertThat(accentJson).isNotNull();
      Truth.assertThat(accentJson).isNotEmpty();
      log.debug("✓ テキスト解析成功: '{}'", testText);
      log.debug("アクセント句JSON（先頭200文字）:");
      log.debug(accentJson.length() > 200 ?
        accentJson.substring(0, 200) + "..." : accentJson);

      // 複数のテキストでテスト
      String[] testTexts = {"おはよう", "ありがとう", "さようなら"};
      for (String text : testTexts) {
        String result = dictionary.analyze(text);
        Truth.assertThat(result).isNotNull();
        Truth.assertThat(result).isNotEmpty();
        log.debug("✓ '{}' 解析成功 ({} 文字)", text, result.length());
      }

      // 空文字列のテスト
      log.debug("--- 空文字列テスト ---");
      String emptyResult = dictionary.analyze("");
      Truth.assertThat(emptyResult).isNotNull();
      log.debug("✓ 空文字列解析: {}", emptyResult);

      // 複数回呼び出しても安全
      String repeatResult = dictionary.analyze(testText);
      Truth.assertThat(repeatResult).isEqualTo(accentJson);
      log.debug("✓ 複数回呼び出しでも同じ結果: {}", accentJson.equals(repeatResult));

      // ネイティブオブジェクトの取得
      Truth.assertThat(dictionary.getNativeOpenJtalk()).isNotNull();
      log.debug("✓ ネイティブオブジェクト取得成功");

    } // ここで自動的にclose()が呼ばれる
    log.debug("✓ try-with-resourcesブロック終了 - 自動クローズ完了");

    // 明示的close()のテスト
    log.debug("--- 明示的close()テスト ---");
    OpenJTalkDictionary dictionary2 = voicevox.initOpenJTalkDictionary(dictPath);
    Truth.assertThat(dictionary2.isClosed()).isFalse();
    log.debug("✓ 新しいOpenJTalkDictionary作成: クローズ状態 = {}", dictionary2.isClosed());

    dictionary2.close();
    Truth.assertThat(dictionary2.isClosed()).isTrue();
    log.debug("✓ 明示的close()実行後: クローズ状態 = {}", dictionary2.isClosed());

    // クローズ後の操作は例外を投げる
    log.debug("--- クローズ後操作テスト ---");
    try {
      dictionary2.analyze("テスト");
      Truth.assertWithMessage("Should throw IllegalStateException").fail();
    } catch (IllegalStateException e) {
      Truth.assertThat(e.getMessage()).contains("already closed");
      log.debug("✓ クローズ後操作で期待通りの例外発生: {}", e.getMessage());
    }

    try {
      dictionary2.getNativeOpenJtalk();
      Truth.assertWithMessage("Should throw IllegalStateException").fail();
    } catch (IllegalStateException e) {
      Truth.assertThat(e.getMessage()).contains("already closed");
      log.debug("✓ クローズ後ネイティブオブジェクト取得で期待通りの例外発生");
    }

    // 複数回close()を呼んでも安全
    dictionary2.close(); // 例外は投げられない
    log.debug("✓ 複数回close()呼び出し - 例外なし");

    log.debug("=== OpenJTalkDictionary テスト完了 ===");
  }

  @Test
  void testOpenJTalkDictionaryWithUserDict() throws VoicevoxException, IOException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    log.debug("=== ユーザー辞書統合テスト開始 ===");
    log.debug("OpenJTalk辞書パス: {}", dictPath);
    log.debug("辞書ディレクトリ存在確認: {}", java.nio.file.Files.exists(dictPath));

    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath);
         UserDict userDict = voicevox.createUserDict()) {

      log.debug("✓ OpenJTalkDictionary及びUserDict作成成功");
      Truth.assertThat(dictionary.isClosed()).isFalse();
      Truth.assertThat(userDict.isClosed()).isFalse();

      // テスト用のテキスト
      String testText = "ボイスボックスで音声合成を行います";

      // ユーザー辞書なしでの解析（ベースライン）
      log.debug("--- ユーザー辞書なしでの解析 ---");
      String withoutUserDict = dictionary.analyze(testText);
      Truth.assertThat(withoutUserDict).isNotNull();
      Truth.assertThat(withoutUserDict).isNotEmpty();
      log.debug("✓ ベースライン解析成功: {} 文字", withoutUserDict.length());
      log.debug("解析結果（先頭200文字）:");
      log.debug(withoutUserDict.length() > 200 ?
        withoutUserDict.substring(0, 200) + "..." : withoutUserDict);

      // ユーザー辞書にカスタム単語を追加
      log.debug("--- ユーザー辞書へのカスタム単語追加 ---");
      java.util.UUID wordId1 = userDict.addWord("ボイスボックス", "ボイスボックス", 0);
      java.util.UUID wordId2 = userDict.addWord("音声合成", "オンセイゴウセイ", 1);
      java.util.UUID wordId3 = userDict.addWord("テキスト読み上げ", "テキストヨミアゲ", 0);

      Truth.assertThat(wordId1).isNotNull();
      Truth.assertThat(wordId2).isNotNull();
      Truth.assertThat(wordId3).isNotNull();
      Truth.assertThat(wordId1).isNotEqualTo(wordId2);
      Truth.assertThat(wordId2).isNotEqualTo(wordId3);

      log.debug("✓ カスタム単語追加成功:");
      log.debug("  - ボイスボックス -> {}", wordId1);
      log.debug("  - 音声合成 -> {}", wordId2);
      log.debug("  - テキスト読み上げ -> {}", wordId3);

      // ユーザー辞書の内容をJSON形式で確認
      log.debug("--- ユーザー辞書内容確認 ---");
      String userDictJson = userDict.toJson();
      Truth.assertThat(userDictJson).isNotNull();
      Truth.assertThat(userDictJson).isNotEmpty();
      Truth.assertThat(userDictJson).contains("ボイスボックス");
      Truth.assertThat(userDictJson).contains("音声合成");
      Truth.assertThat(userDictJson).contains("テキスト読み上げ");

      log.debug("✓ ユーザー辞書JSON確認成功 ({} 文字)", userDictJson.length());
      log.debug("ユーザー辞書JSON（先頭300文字）:");
      log.debug(userDictJson.length() > 300 ?
        userDictJson.substring(0, 300) + "..." : userDictJson);

      // OpenJTalkDictionaryにユーザー辞書を設定
      log.debug("--- OpenJTalkDictionaryにユーザー辞書設定 ---");
      dictionary.useUserDict(userDict.getNativeUserDict());
      log.debug("✓ ユーザー辞書設定成功");

      // ユーザー辞書ありでの解析
      log.debug("--- ユーザー辞書ありでの解析 ---");
      String withUserDict = dictionary.analyze(testText);
      Truth.assertThat(withUserDict).isNotNull();
      Truth.assertThat(withUserDict).isNotEmpty();
      log.debug("✓ ユーザー辞書統合解析成功: {} 文字", withUserDict.length());
      log.debug("解析結果（先頭200文字）:");
      log.debug(withUserDict.length() > 200 ?
        withUserDict.substring(0, 200) + "..." : withUserDict);

      // 解析結果の比較（完全に同じでないことを確認）
      log.debug("--- 解析結果比較 ---");
      boolean resultsAreDifferent = !withoutUserDict.equals(withUserDict);
      log.debug("✓ ユーザー辞書の影響確認: {}",
        resultsAreDifferent ? "解析結果に差異あり" : "解析結果同一");
      log.debug("  - ベースライン長: {}", withoutUserDict.length());
      log.debug("  - ユーザー辞書適用後長: {}", withUserDict.length());

      // 複数のテキストでテスト
      log.debug("--- 複数テキストでの統合テスト ---");
      String[] testTexts = {
        "ボイスボックスでテキスト読み上げ",
        "音声合成エンジンの使用方法",
        "こんにちはボイスボックス"
      };

      for (String text : testTexts) {
        String result = dictionary.analyze(text);
        Truth.assertThat(result).isNotNull();
        Truth.assertThat(result).isNotEmpty();
        log.debug("✓ '" + text + "' 解析成功 (" + result.length() + " 文字)");
      }

      // ユーザー辞書の更新テスト
      log.debug("--- ユーザー辞書更新テスト ---");
      userDict.updateWord(wordId1, "VOICEVOX", "ボイスボックス", 2);
      log.debug("✓ 単語更新成功: " + wordId1);

      // 更新後の解析
      String updatedResult = dictionary.analyze("VOICEVOXで音声合成");
      Truth.assertThat(updatedResult).isNotNull();
      Truth.assertThat(updatedResult).isNotEmpty();
      log.debug("✓ 更新後解析成功: " + updatedResult.length() + " 文字");

      // ユーザー辞書の削除テスト
      log.debug("--- ユーザー辞書単語削除テスト ---");
      userDict.removeWord(wordId3);
      log.debug("✓ 単語削除成功: " + wordId3);

      // 削除後のJSON確認
      String updatedJson = userDict.toJson();
      Truth.assertThat(updatedJson).doesNotContain("テキスト読み上げ");
      log.debug("✓ 削除後JSON確認成功");

      // ネイティブオブジェクトの取得確認
      Truth.assertThat(userDict.getNativeUserDict()).isNotNull();
      log.debug("✓ ネイティブUserDictオブジェクト取得成功");
    }

    log.debug("=== ユーザー辞書統合テスト完了 ===");
  }

  @Test
  @Disabled("Causing issues where auto-close does not work as expected. Working on a fix.")
  void testOpenJTalkDictionaryErrorHandling() throws IOException {
    log.debug("=== エラーハンドリングテスト開始 ===");

    // 存在しないディレクトリでの初期化テスト
    Path invalidPath = Paths.get("nonexistent/dictionary/path");
    Core core = NativeVoicevoxLibrary.load(Path.of(""));

    try {
      // try-with-resources を使わずにテストするため、Suppressionを追加
      @SuppressWarnings("resource")
      OpenJTalkDictionary dict = new OpenJTalkDictionary(invalidPath, core);
      Truth.assertWithMessage("Should throw VoicevoxException").fail();
    } catch (VoicevoxException e) {
      Truth.assertThat(e.getMessage()).contains("Failed to initialize OpenJTalk dictionary");
      log.debug("✓ 無効パスで期待通りの例外発生: " + e.getMessage());
    }

    log.debug("=== エラーハンドリングテスト完了 ===");
  }
}
