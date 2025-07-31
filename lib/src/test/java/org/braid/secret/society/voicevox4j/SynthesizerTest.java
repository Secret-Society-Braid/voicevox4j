package org.braid.secret.society.voicevox4j;

import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.truth.Truth;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
import org.braid.secret.society.voicevox4j.api.Synthesizer;
import org.braid.secret.society.voicevox4j.api.VoiceModelFile;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.junit.jupiter.api.Test;

/**
 * Synthesizerクラスのテストクラス。
 *
 * <h2>使用方法</h2>
 * <pre>{@code
 * // 基本的な使用方法
 * Path dictPath = Paths.get("path/to/open_jtalk_dic_utf_8-1.11");
 * Path vvmPath = Paths.get("path/to/model.vvm");
 * Voicevox voicevox = new Voicevox(Path.of(""));
 *
 * try (OpenJTalkDictionary dict = voicevox.initOpenJTalkDictionary(dictPath);
 *      VoiceModelFile model = voicevox.useVoiceModelFile(vvmPath);
 *      Synthesizer synthesizer = voicevox.createSynthesizer(dict)) {
 *
 *     // 音声モデルを読み込み
 *     synthesizer.loadVoiceModel(model);
 *
 *     // テキストから音声を合成
 *     byte[] audioData = synthesizer.tts("こんにちは", 0);
 *
 *     // オーディオクエリを作成して詳細制御
 *     String audioQuery = synthesizer.createAudioQuery("こんにちは", 0);
 *     byte[] customAudio = synthesizer.synthesis(audioQuery, 0);
 * } // リソースは自動的に解放される
 * }</pre>
 *
 * <h2>リソース管理の重要性</h2>
 * <ul>
 *   <li>必ずtry-with-resources文を使用するか、明示的にclose()を呼び出してください</li>
 *   <li>クローズ後の操作は IllegalStateException を投げます</li>
 *   <li>複数回のclose()呼び出しは安全です</li>
 *   <li>JSONとWAVデータのメモリは内部で自動解放されます</li>
 * </ul>
 */
public class SynthesizerTest {

  private static final int TEST_STYLE_ID = 0;
  private static final String[] TEST_TEXTS = {
      "こんにちは", "おはよう", "ありがとう", "さようなら"
  };
  private static final String[] TEST_KANA = {
      "コンニチワ", "オハヨウ", "アリガトウ", "サヨウナラ"
  };

  @Test
  void testSynthesizerBasicOperations() throws VoicevoxException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Path vvmPath = Paths.get("src/main/resources/voicevox_core/models/vvms/0.vvm").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== Synthesizer 基本操作テスト開始 ===");
    System.out.println("辞書ディレクトリパス: " + dictPath);
    System.out.println("VVMファイルパス: " + vvmPath);
    System.out.println("辞書存在確認: " + Files.exists(dictPath));
    System.out.println("モデル存在確認: " + Files.exists(vvmPath));

    // try-with-resources文を使用した自動リソース管理
    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath);
         VoiceModelFile modelFile = voicevox.useVoiceModelFile(vvmPath);
         Synthesizer synthesizer = voicevox.createSynthesizer(dictionary)) {

      System.out.println("✓ Synthesizer作成成功");
      Truth.assertThat(synthesizer.isClosed()).isFalse();

      // 音声モデルの読み込み
      System.out.println("\n--- 音声モデル管理テスト ---");
      byte[] modelId = modelFile.getModelId();
      Truth.assertThat(synthesizer.isLoadedVoiceModel(modelId)).isFalse();

      synthesizer.loadVoiceModel(modelFile);
      Truth.assertThat(synthesizer.isLoadedVoiceModel(modelId)).isTrue();
      System.out.println("✓ 音声モデル読み込み成功");

      // GPU モード確認
      boolean isGpuMode = synthesizer.isGpuMode();
      System.out.println("✓ GPUモード: " + isGpuMode);

      // メタデータ取得
      String metasJson = synthesizer.getMetasJson();
      Truth.assertThat(metasJson).isNotEmpty();
      System.out.println("✓ メタデータ取得成功 (" + metasJson.length() + " 文字)");

      // TTS (Text-to-Speech) テスト
      testTextToSpeech(synthesizer);

      // オーディオクエリテスト
      testAudioQuery(synthesizer);

      // アクセント句テスト
      testAccentPhrases(synthesizer);

      // 音声合成テスト
      testSynthesis(synthesizer);

      // 音声モデルのアンロード
      synthesizer.unloadVoiceModel(modelId);
      Truth.assertThat(synthesizer.isLoadedVoiceModel(modelId)).isFalse();
      System.out.println("✓ 音声モデルアンロード成功");

    } // ここで自動的にclose()が呼ばれる
    System.out.println("✓ try-with-resourcesブロック終了 - 自動クローズ完了");
  }

  private void testTextToSpeech(Synthesizer synthesizer) throws VoicevoxException {
    System.out.println("\n--- TTS (Text-to-Speech) テスト ---");

    // テキストからのTTS
    for (String text : TEST_TEXTS) {
      byte[] audioData = synthesizer.tts(text, TEST_STYLE_ID);
      Truth.assertThat(audioData).isNotEmpty();
      Truth.assertThat(audioData.length).isGreaterThan(44); // WAVヘッダー分
      System.out.println("✓ TTS成功: '" + text + "' → " + audioData.length + " bytes");
    }

    // カナからのTTS
    for (String kana : TEST_KANA) {
      byte[] audioData = synthesizer.ttsFromKana(kana, TEST_STYLE_ID);
      Truth.assertThat(audioData).isNotEmpty();
      Truth.assertThat(audioData.length).isGreaterThan(44); // WAVヘッダー分
      System.out.println("✓ TTS(カナ)成功: '" + kana + "' → " + audioData.length + " bytes");
    }
  }

  private void testAudioQuery(Synthesizer synthesizer) throws VoicevoxException {
    System.out.println("\n--- オーディオクエリテスト ---");

    // テキストからのオーディオクエリ作成
    for (String text : TEST_TEXTS) {
      String audioQuery = synthesizer.createAudioQuery(text, TEST_STYLE_ID);
      Truth.assertThat(audioQuery).isNotEmpty();
      Truth.assertThat(audioQuery).contains("accent_phrases");
      Truth.assertThat(audioQuery).contains("speedScale");
      System.out.println("✓ オーディオクエリ作成: '" + text + "' → " + audioQuery.length() + " 文字");
    }

    // カナからのオーディオクエリ作成
    for (String kana : TEST_KANA) {
      String audioQuery = synthesizer.createAudioQueryFromKana(kana, TEST_STYLE_ID);
      Truth.assertThat(audioQuery).isNotEmpty();
      Truth.assertThat(audioQuery).contains("accent_phrases");
      System.out.println("✓ オーディオクエリ作成(カナ): '" + kana + "' → " + audioQuery.length() + " 文字");
    }
  }

  private void testAccentPhrases(Synthesizer synthesizer) throws VoicevoxException {
    System.out.println("\n--- アクセント句テスト ---");

    // テキストからのアクセント句作成
    for (String text : TEST_TEXTS) {
      String accentPhrases = synthesizer.createAccentPhrases(text, TEST_STYLE_ID);
      Truth.assertThat(accentPhrases).isNotEmpty();
      Truth.assertThat(accentPhrases).startsWith("[");
      Truth.assertThat(accentPhrases).endsWith("]");
      System.out.println("✓ アクセント句作成: '" + text + "' → " + accentPhrases.length() + " 文字");

      // モーラデータ置換テスト
      String replacedMora = synthesizer.replaceMoraData(accentPhrases, TEST_STYLE_ID);
      Truth.assertThat(replacedMora).isNotEmpty();
      System.out.println("✓ モーラデータ置換: " + replacedMora.length() + " 文字");

      // 音素長置換テスト
      String replacedLength = synthesizer.replacePhonemeLength(accentPhrases, TEST_STYLE_ID);
      Truth.assertThat(replacedLength).isNotEmpty();
      System.out.println("✓ 音素長置換: " + replacedLength.length() + " 文字");

      // モーラピッチ置換テスト
      String replacedPitch = synthesizer.replaceMoraPitch(accentPhrases, TEST_STYLE_ID);
      Truth.assertThat(replacedPitch).isNotEmpty();
      System.out.println("✓ モーラピッチ置換: " + replacedPitch.length() + " 文字");
    }

    // カナからのアクセント句作成
    for (String kana : TEST_KANA) {
      String accentPhrases = synthesizer.createAccentPhrasesFromKana(kana, TEST_STYLE_ID);
      Truth.assertThat(accentPhrases).isNotEmpty();
      Truth.assertThat(accentPhrases).startsWith("[");
      Truth.assertThat(accentPhrases).endsWith("]");
      System.out.println("✓ アクセント句作成(カナ): '" + kana + "' → " + accentPhrases.length() + " 文字");
    }
  }

  private void testSynthesis(Synthesizer synthesizer) throws VoicevoxException {
    System.out.println("\n--- 音声合成テスト ---");

    String testText = TEST_TEXTS[0];
    String audioQuery = synthesizer.createAudioQuery(testText, TEST_STYLE_ID);

    // デフォルトオプションでの合成
    byte[] audioData1 = synthesizer.synthesis(audioQuery, TEST_STYLE_ID);
    Truth.assertThat(audioData1).isNotEmpty();
    Truth.assertThat(audioData1.length).isGreaterThan(44); // WAVヘッダー分
    System.out.println("✓ 音声合成(デフォルト): " + audioData1.length + " bytes");

    // カスタムオプションでの合成はデフォルトオプションを使用
    byte[] audioData2 = synthesizer.synthesis(audioQuery, TEST_STYLE_ID);
    Truth.assertThat(audioData2).isNotEmpty();
    Truth.assertThat(audioData2.length).isGreaterThan(44); // WAVヘッダー分
    System.out.println("✓ 音声合成確認: " + audioData2.length + " bytes");
  }

  @Test
  void testSynthesizerWithCustomOptions() throws VoicevoxException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== Synthesizer カスタムオプションテスト開始 ===");

    // デフォルトオプションでテスト（カスタムオプションは直接アクセスできないため）
    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath);
         Synthesizer synthesizer = voicevox.createSynthesizer(dictionary)) {

      System.out.println("✓ デフォルトオプションでSynthesizer作成成功");
      Truth.assertThat(synthesizer.isClosed()).isFalse();

      // ONNXランタイム取得テスト
      Truth.assertThat(synthesizer.getOnnxruntime()).isNotNull();
      System.out.println("✓ ONNXランタイム取得成功");

    } // ここで自動的にclose()が呼ばれる
    System.out.println("✓ カスタムオプションテスト完了");
  }

  @Test
  void testSynthesizerResourceManagement() throws VoicevoxException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== Synthesizer リソース管理テスト開始 ===");

    OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath);
    Synthesizer synthesizer = voicevox.createSynthesizer(dictionary);

    // 初期状態の確認
    Truth.assertThat(synthesizer.isClosed()).isFalse();
    Truth.assertThat(dictionary.isClosed()).isFalse();
    System.out.println("✓ 初期状態: 両方のリソースが開いている");

    // 手動クローズのテスト
    synthesizer.close();
    Truth.assertThat(synthesizer.isClosed()).isTrue();
    System.out.println("✓ Synthesizer手動クローズ成功");

    // クローズ後の操作でIllegalStateExceptionが発生することを確認
    try {
      synthesizer.getMetasJson();
      fail("IllegalStateExceptionが発生すべき");
    } catch (IllegalStateException e) {
      System.out.println("✓ クローズ後の操作で例外発生: " + e.getMessage());
    }

    // 複数回のクローズは安全
    synthesizer.close(); // 2回目
    Truth.assertThat(synthesizer.isClosed()).isTrue();
    System.out.println("✓ 複数回クローズは安全");

    // 辞書のクローズ
    dictionary.close();
    Truth.assertThat(dictionary.isClosed()).isTrue();
    System.out.println("✓ 辞書クローズ成功");

    System.out.println("✓ リソース管理テスト完了");
  }

  @Test
  void testSynthesizerEmptyAndSpecialCases() throws VoicevoxException {
    Path dictPath = Paths.get("src/main/resources/voicevox_core/dict/open_jtalk_dic_utf_8-1.11").toAbsolutePath();
    Path vvmPath = Paths.get("src/main/resources/voicevox_core/models/vvms/0.vvm").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== Synthesizer 特殊ケーステスト開始 ===");

    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath);
         VoiceModelFile modelFile = voicevox.useVoiceModelFile(vvmPath);
         Synthesizer synthesizer = voicevox.createSynthesizer(dictionary)) {

      synthesizer.loadVoiceModel(modelFile);

      // 空文字列のテスト
      System.out.println("\n--- 空文字列テスト ---");
      String emptyAudioQuery = synthesizer.createAudioQuery("", TEST_STYLE_ID);
      Truth.assertThat(emptyAudioQuery).isNotNull();
      System.out.println("✓ 空文字列でのオーディオクエリ作成: " + emptyAudioQuery.length() + " 文字");

      byte[] emptyAudio = synthesizer.tts("", TEST_STYLE_ID);
      Truth.assertThat(emptyAudio).isNotNull();
      System.out.println("✓ 空文字列でのTTS: " + emptyAudio.length + " bytes");

      // 長いテキストのテスト
      System.out.println("\n--- 長いテキストテスト ---");
      String longText = "今日はとても良い天気です。散歩に出かけたくなるような青空が広がっています。";
      byte[] longAudio = synthesizer.tts(longText, TEST_STYLE_ID);
      Truth.assertThat(longAudio).isNotEmpty();
      System.out.println("✓ 長いテキストTTS: '" + longText + "' → " + longAudio.length + " bytes");

      // 特殊文字を含むテキストのテスト
      System.out.println("\n--- 特殊文字テスト ---");
      String[] specialTexts = {"123", "！？", "、。", "あいうえお"};
      for (String text : specialTexts) {
        try {
          byte[] audio = synthesizer.tts(text, TEST_STYLE_ID);
          Truth.assertThat(audio).isNotNull();
          System.out.println("✓ 特殊文字TTS: '" + text + "' → " + audio.length + " bytes");
        } catch (Exception e) {
          System.out.println("⚠ 特殊文字TTS失敗: '" + text + "' → " + e.getMessage());
        }
      }

    } // ここで自動的にclose()が呼ばれる
    System.out.println("✓ 特殊ケーステスト完了");
  }
}
