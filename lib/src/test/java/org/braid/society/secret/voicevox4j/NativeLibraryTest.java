package org.braid.society.secret.voicevox4j;

import com.google.common.truth.Truth;
import com.sun.jna.ptr.PointerByReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.braid.society.secret.voicevox4j.internal.Core;
import org.braid.society.secret.voicevox4j.internal.NativeVoicevoxLibrary;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxResultCode;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxVoiceModelFile;
import org.junit.jupiter.api.Test;

@Slf4j
public class NativeLibraryTest {

  private static final Core core = NativeVoicevoxLibrary.load(Path.of(""));

  @Test
  void testNativeLibraryLoad() {
    Truth.assertThat(core.voicevox_get_version()).isEqualTo("0.16.0");
  }

  @Test
  void testOnnxRuntimeLoad() {
    String versionedFilename = core.voicevox_get_onnxruntime_lib_versioned_filename();
    String unversionedFilename = core.voicevox_get_onnxruntime_lib_unversioned_filename();
    Truth.assertThat(versionedFilename).isNotEmpty();
    Truth.assertThat(unversionedFilename).isNotEmpty();
    Truth.assertThat(unversionedFilename).contains(versionedFilename);
  }

  @Test
  void testVoiceModelFileLoad() {
    // リソースディレクトリ内のVVMファイルパスを取得
    Path vvmPath = Paths.get("src/main/resources/voicevox_core/models/vvms/0.vvm").toAbsolutePath();

    log.debug("=== 直接JNA VoiceModelFile テスト開始 ===");
    log.debug("VVMファイルパス: {}", vvmPath);
    log.debug("ファイル存在確認: {}", java.nio.file.Files.exists(vvmPath));

    // VoicevoxVoiceModelFileを開くための出力参照を準備
    PointerByReference outModel = new PointerByReference();
    log.debug("✓ PointerByReference準備完了");

    // 音声モデルファイルを開く
    int result = core.voicevox_voice_model_file_open(vvmPath.toString(), outModel);
    log.debug("voicevox_voice_model_file_open結果コード: " + result);

    // 正常に開けることを確認
    Truth.assertThat(result).isEqualTo(VoicevoxResultCode.VOICEVOX_RESULT_OK);
    Truth.assertThat(outModel.getValue()).isNotNull();
    log.debug("✓ VVMファイル正常オープン確認");

    // VoicevoxVoiceModelFileオブジェクトを作成
    VoicevoxVoiceModelFile model = new VoicevoxVoiceModelFile(com.sun.jna.Pointer.nativeValue(outModel.getValue()));
    log.debug("✓ VoicevoxVoiceModelFileオブジェクト作成成功");
    log.debug("ネイティブポインタ値: 0x" + Long.toHexString(com.sun.jna.Pointer.nativeValue(outModel.getValue())));

    // モデルIDを取得してテスト
    byte[] modelId = new byte[16];
    core.voicevox_voice_model_file_id(model, modelId);
    log.debug("✓ モデルID取得完了");

    // モデルIDが空でないことを確認
    boolean hasNonZero = false;
    for (byte b : modelId) {
      if (b != 0) {
        hasNonZero = true;
        break;
      }
    }
    Truth.assertThat(hasNonZero).isTrue();
    System.out.print("モデルID (生バイト): ");
    for (int i = 0; i < modelId.length; i++) {
      System.out.printf("%02x", modelId[i] & 0xFF);
      if (i < modelId.length - 1) System.out.print("-");
    }

    // メタデータJSONを取得してテスト
    com.sun.jna.Pointer metasJson = core.voicevox_voice_model_file_create_metas_json(model);
    Truth.assertThat(metasJson).isNotNull();
    log.debug("✓ メタデータJSONポインタ取得成功");

    String metasJsonString = metasJson.getString(0, "UTF-8");
    Truth.assertThat(metasJsonString).isNotEmpty();
    Truth.assertThat(metasJsonString).contains("styles");
    log.debug("✓ JSONパース成功 - サイズ: " + metasJsonString.length() + " 文字");
    log.debug("JSON内容（先頭300文字）:");
    log.debug(metasJsonString.length() > 300 ?
        metasJsonString.substring(0, 300) + "..." : metasJsonString);

    // メモリを解放
    core.voicevox_json_free(metasJson);
    log.debug("✓ JSONメモリ解放完了");

    core.voicevox_voice_model_file_delete(model);
    log.debug("✓ VoiceModelFileメモリ解放完了");
    log.debug("=== 直接JNA VoiceModelFile テスト完了 ===");
  }
}
