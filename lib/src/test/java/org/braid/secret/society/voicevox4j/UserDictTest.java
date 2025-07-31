package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
import org.braid.secret.society.voicevox4j.api.UserDict;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * UserDictクラスのテストクラス。
 *
 * <h2>使用方法</h2>
 * <pre>{@code
 * // 基本的な使用方法
 * Voicevox voicevox = new Voicevox(Path.of(""));
 * try (UserDict userDict = voicevox.createUserDict()) {
 *     // 単語を追加
 *     UUID wordId = userDict.addWord("表記", "ヒョウキ", 0);
 *
 *     // 単語を更新
 *     userDict.updateWord(wordId, "新表記", "シンヒョウキ", 1);
 *
 *     // JSON形式で出力
 *     String json = userDict.toJson();
 *
 *     // 辞書を保存（.dic拡張子）
 *     userDict.save(Paths.get("my_dict.dic"));
 * } // リソースは自動的に解放される
 * }</pre>
 *
 * <h2>リソース管理の重要性</h2>
 * <ul>
 *   <li>必ずtry-with-resources文を使用するか、明示的にclose()を呼び出してください</li>
 *   <li>クローズ後の操作は IllegalStateException を投げます</li>
 *   <li>複数回のclose()呼び出しは安全です</li>
 *   <li>メモリリークを防ぐため、toJsonメソッドで返されるJSONは内部でメモリが自動解放されます</li>
 * </ul>
 *
 * <h2>修正完了</h2>
 * <p>
 * ClassCastExceptionが解決され、UserDict機能が正常に動作するようになりました。
 * module-info.javaとCore.javaの修正により、JNA型変換の問題が解決されています。
 * </p>
 */
public class UserDictTest {

  @TempDir
  Path tempDir;

  @Test
  void testUserDictBasicOperations() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== UserDict 基本操作テスト開始 ===");

    // try-with-resources文を使用した自動リソース管理
    try (UserDict userDict = voicevox.createUserDict()) {
      System.out.println("✓ UserDictの作成成功");

      // 辞書が正常に開かれることを確認
      Truth.assertThat(userDict.isClosed()).isFalse();
      System.out.println("✓ 辞書が開いた状態: " + !userDict.isClosed());

      // 単語追加のテスト
      System.out.println("\n--- 単語追加テスト ---");
      UUID wordId1 = userDict.addWord("表記", "ヒョウキ", 0);
      Truth.assertThat(wordId1).isNotNull();
      System.out.println("✓ 単語追加成功: 表記 -> " + wordId1);

      UUID wordId2 = userDict.addWord("読み方", "ヨミカタ", 1);
      Truth.assertThat(wordId2).isNotNull();
      Truth.assertThat(wordId2).isNotEqualTo(wordId1);
      System.out.println("✓ 単語追加成功: 読み方 -> " + wordId2);

      // JSON出力のテスト
      System.out.println("\n--- JSON出力テスト ---");
      String json = userDict.toJson();
      Truth.assertThat(json).isNotNull();
      Truth.assertThat(json).isNotEmpty();
      Truth.assertThat(json).contains("表記");
      Truth.assertThat(json).contains("読み方");
      System.out.println("✓ JSON出力成功 (" + json.length() + " 文字)");
      System.out.println("JSON内容（先頭200文字）:");
      System.out.println(json.length() > 200 ?
        json.substring(0, 200) + "..." : json);

      // 単語更新のテスト
      System.out.println("\n--- 単語更新テスト ---");
      userDict.updateWord(wordId1, "新表記", "シンヒョウキ", 2);
      System.out.println("✓ 単語更新成功: " + wordId1);

      String updatedJson = userDict.toJson();
      Truth.assertThat(updatedJson).contains("新表記");
      // 「表記」は「新表記」に含まれるので、元の表記が残っていないことを確認するため
      // より具体的な検証を行う
      Truth.assertThat(updatedJson).doesNotContain("\"surface\":\"表記\"");
      System.out.println("✓ 更新後JSON確認成功");

      // 単語削除のテスト
      System.out.println("\n--- 単語削除テスト ---");
      userDict.removeWord(wordId2);
      System.out.println("✓ 単語削除成功: " + wordId2);

      String finalJson = userDict.toJson();
      Truth.assertThat(finalJson).doesNotContain("読み方");
      System.out.println("✓ 削除後JSON確認成功");

      // ネイティブオブジェクトの取得
      Truth.assertThat(userDict.getNativeUserDict()).isNotNull();
      System.out.println("✓ ネイティブオブジェクト取得成功");

    } // ここで自動的にclose()が呼ばれる
    System.out.println("✓ try-with-resourcesブロック終了 - 自動クローズ完了");

    System.out.println("=== UserDict 基本操作テスト完了 ===");
  }

  @Test
  void testUserDictFileOperations() throws VoicevoxException, IOException {
    Voicevox voicevox = new Voicevox(Path.of(""));
    Path dictFile = tempDir.resolve("test_dict.dic"); // .json から .dic に変更

    System.out.println("=== UserDict ファイル操作テスト開始 ===");
    System.out.println("テスト用辞書ファイル: " + dictFile);

    // 辞書作成と保存
    try (UserDict userDict = voicevox.createUserDict()) {
      System.out.println("✓ UserDict作成成功");

      // テストデータを追加
      UUID wordId1 = userDict.addWord("テスト", "テスト", 0);
      UUID wordId2 = userDict.addWord("保存", "ホゾン", 1);
      System.out.println("✓ テストデータ追加完了");

      // 辞書を保存
      userDict.save(dictFile);
      Truth.assertThat(Files.exists(dictFile)).isTrue();
      Truth.assertThat(Files.size(dictFile)).isGreaterThan(0L);
      System.out.println("✓ 辞書保存成功: " + Files.size(dictFile) + " バイト");
    }

    // 新しい辞書で読み込み
    try (UserDict loadedDict = voicevox.createUserDict()) {
      System.out.println("\n--- 辞書読み込みテスト ---");

      loadedDict.load(dictFile);
      System.out.println("✓ 辞書読み込み成功");

      String loadedJson = loadedDict.toJson();
      Truth.assertThat(loadedJson).contains("テスト");
      Truth.assertThat(loadedJson).contains("保存");
      System.out.println("✓ 読み込み内容確認成功");
      System.out.println("読み込んだJSON（先頭200文字）:");
      System.out.println(loadedJson.length() > 200 ?
        loadedJson.substring(0, 200) + "..." : loadedJson);
    }

    System.out.println("=== UserDict ファイル操作テスト完了 ===");
  }

  @Test
  void testUserDictImport() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== UserDict インポートテスト開始 ===");

    try (UserDict sourceDict = voicevox.createUserDict();
         UserDict targetDict = voicevox.createUserDict()) {

      System.out.println("✓ ソース・ターゲット辞書作成成功");

      // ソース辞書にデータを追加
      sourceDict.addWord("ソース", "ソース", 0);
      sourceDict.addWord("データ", "データ", 1);
      System.out.println("✓ ソース辞書にデータ追加完了");

      // ターゲット辞書にもデータを追加
      targetDict.addWord("ターゲット", "ターゲット", 0);
      System.out.println("✓ ターゲット辞書にデータ追加完了");

      // インポート実行
      targetDict.importFrom(sourceDict);
      System.out.println("✓ インポート実行成功");

      // インポート結果確認
      String resultJson = targetDict.toJson();
      Truth.assertThat(resultJson).contains("ソース");
      Truth.assertThat(resultJson).contains("データ");
      Truth.assertThat(resultJson).contains("ターゲット");
      System.out.println("✓ インポート結果確認成功");
      System.out.println("インポート後JSON（先頭300文字）:");
      System.out.println(resultJson.length() > 300 ?
        resultJson.substring(0, 300) + "..." : resultJson);
    }

    System.out.println("=== UserDict インポートテスト完了 ===");
  }

  @Test
  void testUserDictClosedStateHandling() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== UserDict クローズ状態処理テスト開始 ===");

    UserDict userDict = voicevox.createUserDict();
    System.out.println("✓ UserDict作成成功");

    // 正常な操作を確認
    Truth.assertThat(userDict.isClosed()).isFalse();
    UUID wordId = userDict.addWord("テスト", "テスト", 0);
    Truth.assertThat(wordId).isNotNull();
    System.out.println("✓ クローズ前の正常操作確認");

    // 明示的にクローズ
    userDict.close();
    Truth.assertThat(userDict.isClosed()).isTrue();
    System.out.println("✓ 明示的クローズ実行");

    // クローズ後の操作でIllegalStateExceptionが投げられることを確認
    try {
      userDict.addWord("失敗", "シッパイ", 0);
      Truth.assertWithMessage("クローズ後の操作で例外が投げられるべき").fail();
    } catch (IllegalStateException e) {
      System.out.println("✓ クローズ後の操作で期待通り例外発生: " + e.getMessage());
    }

    try {
      userDict.toJson();
      Truth.assertWithMessage("クローズ後の操作で例外が投げられるべき").fail();
    } catch (IllegalStateException e) {
      System.out.println("✓ クローズ後のJSON出力で期待通り例外発生");
    }

    // 複数回のclose()は安全
    userDict.close(); // 2回目
    userDict.close(); // 3回目
    System.out.println("✓ 複数回のclose()は安全");

    System.out.println("=== UserDict クローズ状態処理テスト完了 ===");
  }

  @Test
  void testUserDictEdgeCases() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== UserDict エッジケーステスト開始 ===");

    try (UserDict userDict = voicevox.createUserDict()) {
      System.out.println("✓ UserDict作成成功");

      // 空の辞書でJSON出力
      String emptyJson = userDict.toJson();
      Truth.assertThat(emptyJson).isNotNull();
      Truth.assertThat(emptyJson).isNotEmpty();
      System.out.println("✓ 空辞書JSON出力成功: " + emptyJson);

      // 特殊文字を含む単語の追加
      System.out.println("\n--- 特殊文字テスト ---");
      UUID specialId = userDict.addWord("特殊文字！", "トクシュモジ", 0);
      Truth.assertThat(specialId).isNotNull();
      System.out.println("✓ 特殊文字を含む単語追加成功");

      // 長い文字列の追加
      String longSurface = "これは非常に長い表記の例ですがちゃんと処理されるはずです";
      String longPronunciation = "コレワヒジョウニナガイヒョウキノレイデスガチャントショリサレルハズデス";
      UUID longId = userDict.addWord(longSurface, longPronunciation, 3);
      Truth.assertThat(longId).isNotNull();
      System.out.println("✓ 長い文字列の単語追加成功");

      // アクセント型の境界値テスト（適切な範囲に修正）
      UUID maxAccentId = userDict.addWord("最大アクセント", "サイダイアクセント", 9);
      Truth.assertThat(maxAccentId).isNotNull();
      System.out.println("✓ 最大アクセント型値での追加成功");

      // 最終的なJSON確認
      String finalJson = userDict.toJson();
      Truth.assertThat(finalJson).contains("特殊文字！");
      Truth.assertThat(finalJson).contains(longSurface);
      Truth.assertThat(finalJson).contains("最大アクセント");
      System.out.println("✓ 全ての特殊ケースがJSON内に存在確認");
    }

    System.out.println("=== UserDict エッジケーステスト完了 ===");
  }

  @Test
  void testUserDictFileFormats() throws VoicevoxException, IOException {
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== UserDict ファイル形式テスト開始 ===");

    // サンプル辞書データを作成
    try (UserDict sourceDict = voicevox.createUserDict()) {
      System.out.println("✓ ソース辞書作成成功");

      // 様々な種類の単語を追加
      sourceDict.addWord("人工知能", "ジンコウチノウ", 0);
      sourceDict.addWord("機械学習", "キカイガクシュウ", 1);
      sourceDict.addWord("ディープラーニング", "ディープラーニング", 2);
      sourceDict.addWord("ニューラルネットワーク", "ニューラルネットワーク", 1);
      System.out.println("✓ テスト用単語群追加完了");

      // JSON形式での確認
      String dictJson = sourceDict.toJson();
      Truth.assertThat(dictJson).contains("人工知能");
      Truth.assertThat(dictJson).contains("機械学習");
      Truth.assertThat(dictJson).contains("ディープラーニング");
      Truth.assertThat(dictJson).contains("ニューラルネットワーク");
      System.out.println("✓ JSON出力に全単語が含まれることを確認");
      System.out.println("辞書JSON長: " + dictJson.length() + " 文字");

      // ファイル保存
      Path dictFile = tempDir.resolve("tech_terms.dic"); // .json から .dic に変更
      sourceDict.save(dictFile);
      Truth.assertThat(Files.exists(dictFile)).isTrue();
      long fileSize = Files.size(dictFile);
      Truth.assertThat(fileSize).isGreaterThan(0L);
      System.out.println("✓ 辞書ファイル保存成功: " + fileSize + " バイト");

      // ファイル内容の確認
      String fileContent = Files.readString(dictFile);
      Truth.assertThat(fileContent).contains("人工知能");
      Truth.assertThat(fileContent).contains("機械学習");
      System.out.println("✓ 保存されたファイル内容確認成功");
    }

    System.out.println("=== UserDict ファイル形式テスト完了 ===");
  }

  @Test
  void testUserDictWordManagement() throws VoicevoxException {
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== UserDict 単語管理テスト開始 ===");

    try (UserDict userDict = voicevox.createUserDict()) {
      System.out.println("✓ UserDict作成成功");

      // 段階的な単語追加と管理
      System.out.println("\n--- 段階的単語管理 ---");

      // 第1段階: 基本単語追加
      UUID word1 = userDict.addWord("初期単語", "ショキタンゴ", 0);
      String json1 = userDict.toJson();
      Truth.assertThat(json1).contains("初期単語");
      System.out.println("✓ 第1段階完了: " + word1);

      // 第2段階: 追加の単語
      UUID word2 = userDict.addWord("追加単語", "ツイカタンゴ", 1);
      UUID word3 = userDict.addWord("第三単語", "ダイサンタンゴ", 2);
      String json2 = userDict.toJson();
      Truth.assertThat(json2).contains("初期単語");
      Truth.assertThat(json2).contains("追加単語");
      Truth.assertThat(json2).contains("第三単語");
      System.out.println("✓ 第2段階完了: " + word2 + ", " + word3);

      // 第3段階: 単語更新
      userDict.updateWord(word2, "更新単語", "コウシンタンゴ", 3);
      String json3 = userDict.toJson();
      Truth.assertThat(json3).contains("更新単語");
      Truth.assertThat(json3).doesNotContain("追加単語");
      System.out.println("✓ 第3段階完了: 単語更新");

      // 第4段階: 選択的削除
      userDict.removeWord(word1);
      String json4 = userDict.toJson();
      Truth.assertThat(json4).doesNotContain("初期単語");
      Truth.assertThat(json4).contains("更新単語");
      Truth.assertThat(json4).contains("第三単語");
      System.out.println("✓ 第4段階完了: 選択的削除");

      // 最終確認
      Truth.assertThat(json4).doesNotContain("初期単語");
      Truth.assertThat(json4).doesNotContain("追加単語");
      Truth.assertThat(json4).contains("更新単語");
      Truth.assertThat(json4).contains("第三単語");
      System.out.println("✓ 最終状態確認成功");
      System.out.println("最終JSON長: " + json4.length() + " 文字");
    }

    System.out.println("=== UserDict 単語管理テスト完了 ===");
  }

  @Test
  void testUserDictCreationOnly() {
    System.out.println("=== UserDict 作成のみテスト開始 ===");

    try {
      Voicevox voicevox = new Voicevox(Path.of(""));
      System.out.println("✓ Voicevox初期化成功");

      UserDict userDict = voicevox.createUserDict();
      System.out.println("✓ UserDict作成成功");

      Truth.assertThat(userDict).isNotNull();
      Truth.assertThat(userDict.isClosed()).isFalse();
      System.out.println("✓ UserDict状態確認成功");

      userDict.close();
      System.out.println("✓ UserDict明示的クローズ成功");

    } catch (Exception e) {
      System.err.println("❌ エラー発生: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      e.printStackTrace();
      throw e;
    }

    System.out.println("=== UserDict 作成のみテスト完了 ===");
  }

  @Test
  void testUserDictJsonOnly() throws VoicevoxException {
    System.out.println("=== UserDict JSON出力のみテスト開始 ===");

    try (UserDict userDict = new Voicevox(Path.of("")).createUserDict()) {
      System.out.println("✓ UserDict作成成功");

      String json = userDict.toJson();
      System.out.println("✓ JSON出力成功: " + json);

      Truth.assertThat(json).isNotNull();
      Truth.assertThat(json).isNotEmpty();

    } catch (Exception e) {
      System.err.println("❌ エラー発生: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      e.printStackTrace();
      throw e;
    }

    System.out.println("=== UserDict JSON出力のみテスト完了 ===");
  }
}
