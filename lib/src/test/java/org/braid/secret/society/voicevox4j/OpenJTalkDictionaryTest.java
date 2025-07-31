package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
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
 *     VoicevoxUserDict userDict = core.voicevox_user_dict_new();
 *     dict.useUserDict(userDict);
 * } // リソースは自動的に解放される
 * }</pre>
 *
 * <h2>リソース管理の重要性</h2>
 * <ul>
 *   <li>必ずtry-with-resources文を使用するか、明示的にclose()を呼び出してください</li>
 *   <li>クローズ後の操作は IllegalStateException を投げます</li>
 *   <li>複数回のclose()呼び出しは安全です</li>
 *   <li>メモリリークを防ぐため、analyzeメソッドで返されるJSONは内部でメモリが自動解放されます</li>
 * </ul>
 */
@Disabled("Dictionary file reading is not implemented yet")
public class OpenJTalkDictionaryTest {

  @Test
  void testOpenJTalkDictionary() throws VoicevoxException, IOException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== OpenJTalkDictionary テスト開始 ===");
    System.out.println("辞書ディレクトリパス: " + dictPath);
    System.out.println("ディレクトリ存在確認: " + java.nio.file.Files.exists(dictPath));
    System.out.println("ディレクトリ確認: " + java.nio.file.Files.isDirectory(dictPath));

    // try-with-resources文を使用した自動リソース管理
    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath)) {
      System.out.println("✓ OpenJTalkDictionaryの作成成功");

      // 辞書が正常に開かれることを確認
      Truth.assertThat(dictionary.isClosed()).isFalse();
      System.out.println("✓ 辞書が開いた状態: " + !dictionary.isClosed());

      // テキスト解析のテスト
      System.out.println("\n--- テキスト解析テスト ---");
      String testText = "こんにちは";
      String accentJson = dictionary.analyze(testText);

      Truth.assertThat(accentJson).isNotNull();
      Truth.assertThat(accentJson).isNotEmpty();
      System.out.println("✓ テキスト解析成功: '" + testText + "'");
      System.out.println("アクセント句JSON（先頭200文字）:");
      System.out.println(accentJson.length() > 200 ?
        accentJson.substring(0, 200) + "..." : accentJson);

      // 複数のテキストでテスト
      String[] testTexts = {"おはよう", "ありがとう", "さようなら"};
      for (String text : testTexts) {
        String result = dictionary.analyze(text);
        Truth.assertThat(result).isNotNull();
        Truth.assertThat(result).isNotEmpty();
        System.out.println("✓ '" + text + "' 解析成功 (" + result.length() + " 文字)");
      }

      // 空文字列のテスト
      System.out.println("\n--- 空文字列テスト ---");
      String emptyResult = dictionary.analyze("");
      Truth.assertThat(emptyResult).isNotNull();
      System.out.println("✓ 空文字列解析: " + emptyResult);

      // 複数回呼び出しても安全
      String repeatResult = dictionary.analyze(testText);
      Truth.assertThat(repeatResult).isEqualTo(accentJson);
      System.out.println("✓ 複数回呼び出しでも同じ結果: " + accentJson.equals(repeatResult));

      // ネイティブオブジェクトの取得
      Truth.assertThat(dictionary.getNativeOpenJtalk()).isNotNull();
      System.out.println("✓ ネイティブオブジェクト取得成功");

    } // ここで自動的にclose()が呼ばれる
    System.out.println("✓ try-with-resourcesブロック終了 - 自動クローズ完了");

    // 明示的close()のテスト
    System.out.println("\n--- 明示的close()テスト ---");
    OpenJTalkDictionary dictionary2 = voicevox.initOpenJTalkDictionary(dictPath);
    Truth.assertThat(dictionary2.isClosed()).isFalse();
    System.out.println("✓ 新しいOpenJTalkDictionary作成: クローズ状態 = " + dictionary2.isClosed());

    dictionary2.close();
    Truth.assertThat(dictionary2.isClosed()).isTrue();
    System.out.println("✓ 明示的close()実行後: クローズ状態 = " + dictionary2.isClosed());

    // クローズ後の操作は例外を投げる
    System.out.println("--- クローズ後操作テスト ---");
    try {
      dictionary2.analyze("テスト");
      Truth.assertWithMessage("Should throw IllegalStateException").fail();
    } catch (IllegalStateException e) {
      Truth.assertThat(e.getMessage()).contains("already closed");
      System.out.println("✓ クローズ後操作で期待通りの例外発生: " + e.getMessage());
    }

    try {
      dictionary2.getNativeOpenJtalk();
      Truth.assertWithMessage("Should throw IllegalStateException").fail();
    } catch (IllegalStateException e) {
      Truth.assertThat(e.getMessage()).contains("already closed");
      System.out.println("✓ クローズ後ネイティブオブジェクト取得で期待通りの例外発生");
    }

    // 複数回close()を呼んでも安全
    dictionary2.close(); // 例外は投げられない
    System.out.println("✓ 複数回close()呼び出し - 例外なし");

    System.out.println("=== OpenJTalkDictionary テスト完了 ===\n");
  }

  @Test
  void testOpenJTalkDictionaryWithUserDict() throws VoicevoxException, IOException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Core core = NativeVoicevoxLibrary.load(Path.of(""));

    System.out.println("=== ユーザー辞書テスト開始 ===");

    try (OpenJTalkDictionary dictionary = new OpenJTalkDictionary(dictPath, core)) {
      // ユーザー辞書の作成とテスト
      // 注意: 実際のユーザー辞書機能をテストするには、VoicevoxUserDictの実装が必要
      System.out.println("✓ OpenJTalkDictionary作成成功（ユーザー辞書テスト用）");

      // 基本的な解析が動作することを確認
      String result = dictionary.analyze("テスト用テキスト");
      Truth.assertThat(result).isNotNull();
      System.out.println("✓ 基本解析成功");

      System.out.println("注意: ユーザー辞書の実際のテストにはVoicevoxUserDictクラスが必要です");
    }

    System.out.println("=== ユーザー辞書テスト完了 ===\n");
  }

  @Test
  void testOpenJTalkDictionaryErrorHandling() throws IOException {
    System.out.println("=== エラーハンドリングテスト開始 ===");

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
      System.out.println("✓ 無効パスで期待通りの例外発生: " + e.getMessage());
    }

    System.out.println("=== エラーハンドリングテスト完了 ===\n");
  }
}
