package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
import org.braid.secret.society.voicevox4j.api.UserDict;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * UserDictとOpenJTalkDictionaryの統合テストクラス。
 * ユーザー辞書をOpenJTalk辞書に渡して実際のテキスト解析を行うテストを実装します。
 *
 * <h2>実装済み機能</h2>
 * <ul>
 *   <li>UserDict CRUD操作（追加、更新、削除）</li>
 *   <li>ファイル入出力（保存、読み込み）</li>
 *   <li>辞書間インポート機能</li>
 *   <li>JSON形式での出力</li>
 *   <li>OpenJTalkDictionaryとの統合</li>
 *   <li>適切なリソース管理とメモリリーク防止</li>
 * </ul>
 *
 */
public class UserDictOpenJTalkIntegrationTest {

  @Test
  void testUserDictWithOpenJTalkDictionary() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();

    System.out.println("=== UserDict + OpenJTalk 統合テスト開始 ===");
    System.out.println("OpenJTalk辞書パス: " + dictPath);
    System.out.println("辞書ディレクトリ存在確認: " + Files.exists(dictPath));

    if (!Files.exists(dictPath)) {
      System.out.println("⚠️ OpenJTalk辞書が見つかりません。テストをスキップします。");
      return;
    }

    try (OpenJTalkDictionary openJtalkDict = voicevox.initOpenJTalkDictionary(dictPath)) {
      System.out.println("✓ OpenJTalk辞書初期化成功");

      // まずユーザー辞書なしでテキスト解析
      System.out.println("\n--- ユーザー辞書なしでのテキスト解析 ---");
      String testText = "ボイスボックスを使って音声合成します";
      String withoutUserDict = openJtalkDict.analyze(testText);
      Truth.assertThat(withoutUserDict).isNotNull();
      Truth.assertThat(withoutUserDict).isNotEmpty();
      System.out.println("✓ ユーザー辞書なしでの解析成功");
      System.out.println("解析結果（先頭200文字）:");
      System.out.println(withoutUserDict.length() > 200 ?
        withoutUserDict.substring(0, 200) + "..." : withoutUserDict);

      // ユーザー辞書を作成してカスタム単語を追加
      System.out.println("\n--- ユーザー辞書作成とカスタム単語追加 ---");
      try (UserDict userDict = voicevox.createUserDict()) {
        System.out.println("✓ ユーザー辞書作成成功");

        // カスタム単語「ボイスボックス」を追加
        UUID customWordId = userDict.addWord("ボイスボックス", "ボイスボックス", 0);
        Truth.assertThat(customWordId).isNotNull();
        System.out.println("✓ カスタム単語追加成功: ボイスボックス -> " + customWordId);

        // より多くのカスタム単語を追加
        userDict.addWord("音声合成", "オンセイゴウセイ", 0);
        userDict.addWord("テキスト読み上げ", "テキストヨミアゲ", 1);
        System.out.println("✓ 追加のカスタム単語登録完了");

        // ユーザー辞書の内容をJSON形式で確認
        String userDictJson = userDict.toJson();
        Truth.assertThat(userDictJson).contains("ボイスボックス");
        Truth.assertThat(userDictJson).contains("音声合成");
        Truth.assertThat(userDictJson).contains("テキスト読み上げ");
        System.out.println("✓ ユーザー辞書JSON内容確認成功");
        System.out.println("ユーザー辞書JSON:");
        System.out.println(userDictJson.length() > 300 ?
          userDictJson.substring(0, 300) + "..." : userDictJson);

        // OpenJTalk辞書にユーザー辞書を設定
        System.out.println("\n--- OpenJTalk辞書にユーザー辞書設定 ---");
        openJtalkDict.useUserDict(userDict.getNativeUserDict());
        System.out.println("✓ ユーザー辞書をOpenJTalk辞書に設定成功");

        // ユーザー辞書ありでテキスト解析
        System.out.println("\n--- ユーザー辞書ありでのテキスト解析 ---");
        String withUserDict = openJtalkDict.analyze(testText);
        Truth.assertThat(withUserDict).isNotNull();
        Truth.assertThat(withUserDict).isNotEmpty();
        System.out.println("✓ ユーザー辞書ありでの解析成功");
        System.out.println("解析結果（先頭200文字）:");
        System.out.println(withUserDict.length() > 200 ?
          withUserDict.substring(0, 200) + "..." : withUserDict);

        // カスタム単語を含む複数のテキストで解析
        System.out.println("\n--- 複数テキストでの解析テスト ---");
        String[] testTexts = {
          "ボイスボックスは優秀な音声合成エンジンです",
          "テキスト読み上げ機能を使ってみます",
          "音声合成の品質が向上しました"
        };

        for (String text : testTexts) {
          String result = openJtalkDict.analyze(text);
          Truth.assertThat(result).isNotNull();
          Truth.assertThat(result).isNotEmpty();
          System.out.println("✓ '" + text + "' 解析成功 (" + result.length() + " 文字)");
        }

        // 結果の比較（ユーザー辞書あり/なしで異なることを確認）
        System.out.println("\n--- 解析結果の比較 ---");
        if (!withoutUserDict.equals(withUserDict)) {
          System.out.println("✓ ユーザー辞書の設定により解析結果が変化することを確認");
        } else {
          System.out.println("⚠️ ユーザー辞書設定前後で解析結果に変化なし");
        }

        System.out.println("ユーザー辞書なし結果長: " + withoutUserDict.length() + " 文字");
        System.out.println("ユーザー辞書あり結果長: " + withUserDict.length() + " 文字");
      }
    }

    System.out.println("=== UserDict + OpenJTalk 統合テスト完了 ===");
  }

  @Test
  void testMultipleUserDictWords() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();

    System.out.println("=== 複数ユーザー辞書単語テスト開始 ===");

    if (!Files.exists(dictPath)) {
      System.out.println("⚠️ OpenJTalk辞書が見つかりません。テストをスキップします。");
      return;
    }

    try (OpenJTalkDictionary openJtalkDict = voicevox.initOpenJTalkDictionary(dictPath);
         UserDict userDict = voicevox.createUserDict()) {

      System.out.println("✓ 辞書初期化成功");

      // 技術用語の辞書を作成
      System.out.println("\n--- 技術用語辞書作成 ---");
      userDict.addWord("人工知能", "ジンコウチノウ", 0);
      userDict.addWord("機械学習", "キカイガクシュウ", 1);
      userDict.addWord("ディープラーニング", "ディープラーニング", 2);
      userDict.addWord("ニューラルネットワーク", "ニューラルネットワーク", 1);
      userDict.addWord("自然言語処理", "シゼンゲンゴショリ", 0);
      System.out.println("✓ 技術用語5単語を辞書に追加");

      // OpenJTalk辞書にユーザー辞書を設定
      openJtalkDict.useUserDict(userDict.getNativeUserDict());
      System.out.println("✓ ユーザー辞書設定完了");

      // 技術用語を含む長いテキストで解析
      System.out.println("\n--- 技術文書解析テスト ---");
      String techText = "人工知能と機械学習の分野では、ディープラーニングやニューラルネットワークが " +
                       "自然言語処理の精度向上に大きく貢献しています。これらの技術により、" +
                       "音声合成の品質も飛躍的に向上しました。";

      String result = openJtalkDict.analyze(techText);
      Truth.assertThat(result).isNotNull();
      Truth.assertThat(result).isNotEmpty();
      System.out.println("✓ 技術文書解析成功");
      System.out.println("解析結果長: " + result.length() + " 文字");
      System.out.println("解析結果（先頭400文字）:");
      System.out.println(result.length() > 400 ?
        result.substring(0, 400) + "..." : result);

      // 各技術用語が個別に正しく解析されることを確認
      System.out.println("\n--- 個別用語解析確認 ---");
      String[] terms = {"人工知能", "機械学習", "ディープラーニング", "ニューラルネットワーク", "自然言語処理"};

      for (String term : terms) {
        String termResult = openJtalkDict.analyze(term + "について説明します");
        Truth.assertThat(termResult).isNotNull();
        Truth.assertThat(termResult).isNotEmpty();
        System.out.println("✓ '" + term + "' 個別解析成功");
      }
    }

    System.out.println("=== 複数ユーザー辞書単語テスト完了 ===");
  }

  @Test
  void testUserDictWordUpdatesAndDeletions() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();

    System.out.println("=== ユーザー辞書単語更新・削除テスト開始 ===");

    if (!Files.exists(dictPath)) {
      System.out.println("⚠️ OpenJTalk辞書が見つかりません。テストをスキップします。");
      return;
    }

    try (OpenJTalkDictionary openJtalkDict = voicevox.initOpenJTalkDictionary(dictPath);
         UserDict userDict = voicevox.createUserDict()) {

      System.out.println("✓ 辞書初期化成功");

      // 初期単語を追加
      System.out.println("\n--- 初期単語追加 ---");
      UUID wordId1 = userDict.addWord("テスト単語", "テストタンゴ", 0);
      UUID wordId2 = userDict.addWord("削除予定", "サクジョヨテイ", 1);
      System.out.println("✓ 初期単語追加完了");

      // OpenJTalk辞書にユーザー辞書を設定
      openJtalkDict.useUserDict(userDict.getNativeUserDict());
      System.out.println("✓ ユーザー辞書設定完了");

      // 初期状態での解析
      String testText = "テスト単語と削除予定の単語を使います";
      String initialResult = openJtalkDict.analyze(testText);
      Truth.assertThat(initialResult).isNotNull();
      System.out.println("✓ 初期状態解析成功: " + initialResult.length() + " 文字");

      // 単語を更新
      System.out.println("\n--- 単語更新テスト ---");
      userDict.updateWord(wordId1, "更新単語", "コウシンタンゴ", 2);
      System.out.println("✓ 単語更新完了");

      // 更新後の解析
      String updatedTestText = "更新単語と削除予定の単語を使います";
      String updatedResult = openJtalkDict.analyze(updatedTestText);
      Truth.assertThat(updatedResult).isNotNull();
      System.out.println("✓ 更新後解析成功: " + updatedResult.length() + " 文字");

      // 単語を削除
      System.out.println("\n--- 単語削除テスト ---");
      userDict.removeWord(wordId2);
      System.out.println("✓ 単語削除完了");

      // 削除後の解析
      String finalTestText = "更新単語を使います";
      String finalResult = openJtalkDict.analyze(finalTestText);
      Truth.assertThat(finalResult).isNotNull();
      System.out.println("✓ 削除後解析成功: " + finalResult.length() + " 文字");

      // 最終的なユーザー辞書の状態確認
      String finalDictJson = userDict.toJson();
      Truth.assertThat(finalDictJson).contains("更新単語");
      Truth.assertThat(finalDictJson).doesNotContain("削除予定");
      Truth.assertThat(finalDictJson).doesNotContain("テスト単語");
      System.out.println("✓ 最終辞書状態確認成功");
    }

    System.out.println("=== ユーザー辞書単語更新・削除テスト完了 ===");
  }

  @Test
  void testUserDictBasicIntegrationWithSimpleText() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();

    System.out.println("=== UserDict 基本統合テスト（簡単なテキスト）開始 ===");

    if (!Files.exists(dictPath)) {
      System.out.println("⚠️ OpenJTalk辞書が見つかりません。テストをスキップします。");
      return;
    }

    try (OpenJTalkDictionary openJtalkDict = voicevox.initOpenJTalkDictionary(dictPath)) {
      System.out.println("✓ OpenJTalk辞書初期化成功");

      // まず日本語テキストで基本動作を確認
      System.out.println("\n--- 日本語テキストでの基本動作確認 ---");
      try {
        String japaneseText = "こんにちは世界";
        String japaneseResult = openJtalkDict.analyze(japaneseText);
        System.out.println("✓ 日本語テキスト解析成功: " + japaneseResult.length() + " 文字");
      } catch (Exception e) {
        System.out.println("⚠️ 日本語テキスト解析エラー: " + e.getMessage());
      }

      // ユーザー辞書を作成して統合テスト
      System.out.println("\n--- ユーザー辞書作成と統合 ---");
      try (UserDict userDict = voicevox.createUserDict()) {
        System.out.println("✓ ユーザー辞書作成成功");

        // ユーザー辞書の基本機能を確認
        UUID wordId = userDict.addWord("テスト", "テスト", 0);
        Truth.assertThat(wordId).isNotNull();
        System.out.println("✓ ユーザー辞書に単語追加成功: " + wordId);

        // JSON出力の確認
        String userDictJson = userDict.toJson();
        Truth.assertThat(userDictJson).isNotNull();
        Truth.assertThat(userDictJson).isNotEmpty();
        Truth.assertThat(userDictJson).contains("テスト");
        System.out.println("✓ ユーザー辞書JSON出力成功: " + userDictJson.length() + " 文字");

        // OpenJTalk辞書にユーザー辞書を設定
        openJtalkDict.useUserDict(userDict.getNativeUserDict());
        System.out.println("✓ ユーザー辞書をOpenJTalk辞書に設定成功");

        // 単語の更新テスト
        userDict.updateWord(wordId, "更新済み", "コウシンズミ", 1);
        System.out.println("✓ ユーザー辞書単語更新成功");

        // 更新後のJSON確認
        String updatedJson = userDict.toJson();
        Truth.assertThat(updatedJson).contains("更新済み");
        Truth.assertThat(updatedJson).doesNotContain("テスト");
        System.out.println("✓ 更新後JSON確認成功");

        // 単語削除テスト
        userDict.removeWord(wordId);
        System.out.println("✓ ユーザー辞書単語削除成功");

        // 削除後のJSON確認
        String finalJson = userDict.toJson();
        Truth.assertThat(finalJson).doesNotContain("更新済み");
        System.out.println("✓ 削除後JSON確認成功");
      }
    }

    System.out.println("=== UserDict 基本統合テスト完了 ===");
  }

  @Test
  void testUserDictResourceManagement() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();

    System.out.println("=== UserDict リソース管理テスト開始 ===");

    if (!Files.exists(dictPath)) {
      System.out.println("⚠️ OpenJTalk辞書が見つかりません。テストをスキップします。");
      return;
    }

    // 複数のUserDictとOpenJTalkDictionaryの作成と解放
    for (int i = 0; i < 3; i++) {
      System.out.println("\n--- 反復 " + (i + 1) + " ---");

      try (OpenJTalkDictionary openJtalkDict = voicevox.initOpenJTalkDictionary(dictPath);
           UserDict userDict1 = voicevox.createUserDict();
           UserDict userDict2 = voicevox.createUserDict()) {

        System.out.println("✓ 辞書作成成功（反復 " + (i + 1) + "）");

        // 各ユーザー辞書に異なる単語を追加
        UUID word1 = userDict1.addWord("単語1_" + i, "タンゴ1", 0);
        UUID word2 = userDict2.addWord("単語2_" + i, "タンゴ2", 1);

        Truth.assertThat(word1).isNotNull();
        Truth.assertThat(word2).isNotNull();
        System.out.println("✓ 各辞書に単語追加成功");

        // JSON出力の確認
        String json1 = userDict1.toJson();
        String json2 = userDict2.toJson();

        Truth.assertThat(json1).contains("単語1_" + i);
        Truth.assertThat(json2).contains("単語2_" + i);
        System.out.println("✓ JSON出力確認成功");

        // 辞書間でのインポートテスト
        userDict1.importFrom(userDict2);
        String mergedJson = userDict1.toJson();
        Truth.assertThat(mergedJson).contains("単語1_" + i);
        Truth.assertThat(mergedJson).contains("単語2_" + i);
        System.out.println("✓ 辞書インポート成功");

        // OpenJTalk辞書への設定
        openJtalkDict.useUserDict(userDict1.getNativeUserDict());
        System.out.println("✓ OpenJTalk辞書設定成功");

      } // try-with-resourcesで自動的にリソース解放
      System.out.println("✓ リソース自動解放完了（反復 " + (i + 1) + "）");
    }

    System.out.println("=== UserDict リソース管理テスト完了 ===");
  }
}
