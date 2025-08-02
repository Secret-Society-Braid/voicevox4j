package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
import org.braid.secret.society.voicevox4j.api.Synthesizer;
import org.braid.secret.society.voicevox4j.api.UserDict;
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
@Slf4j
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
    Voicevox voicevox = new Voicevox(Path.of("src/main/resources/voicevox_core").toAbsolutePath());

    log.debug("=== Synthesizer 基本操作テスト開始 ===");
    log.debug("辞書ディレクトリパス: {}", dictPath);
    log.debug("VVMファイルパス: {}", vvmPath);
    log.debug("辞書存在確認: {}", Files.exists(dictPath));
    log.debug("モデル存在確認: {}", Files.exists(vvmPath));

    // try-with-resources文を使用した自動リソース管理
    try (OpenJTalkDictionary dictionary = voicevox.initOpenJTalkDictionary(dictPath);
         VoiceModelFile modelFile = voicevox.useVoiceModelFile(vvmPath)) {
      log.debug("✓ OpenJTalk辞書の初期化成功");
      UserDict userDict = voicevox.createUserDict();
      dictionary.useUserDict(userDict);
      log.debug("✓ ユーザ辞書の作成成功");
      try (Synthesizer synthesizer = voicevox.createSynthesizer(dictionary)) {
        log.debug("✓ Synthesizer作成成功");
        Truth.assertThat(synthesizer.isClosed()).isFalse();

        // 音声モデルの読み込み
        log.debug("\n--- 音声モデル管理テスト ---");
        byte[] modelId = modelFile.getModelId();
        Truth.assertThat(synthesizer.isLoadedVoiceModel(modelId)).isFalse();

        synthesizer.loadVoiceModel(modelFile);
        Truth.assertThat(synthesizer.isLoadedVoiceModel(modelId)).isTrue();
        log.debug("✓ 音声モデル読み込み成功");

        // GPU モード確認
        boolean isGpuMode = synthesizer.isGpuMode();
        log.debug("✓ GPUモード: {}", isGpuMode);

        // メタデータ取得
        String metasJson = synthesizer.getMetasJson();
        Truth.assertThat(metasJson).isNotEmpty();
        log.debug("✓ メタデータ取得成功 ({} 文字)", metasJson.length());

        // 音声モデルのアンロード
        synthesizer.unloadVoiceModel(modelId);
        Truth.assertThat(synthesizer.isLoadedVoiceModel(modelId)).isFalse();
        log.debug("✓ 音声モデルアンロード成功");
      }
    } // ここで自動的にclose()が呼ばれる
    log.debug("✓ try-with-resourcesブロック終了 - 自動クローズ完了");
  }
}
