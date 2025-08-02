package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import com.sun.jna.ptr.PointerByReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.braid.secret.society.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.NativeVoicevoxLibrary;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxResultCode;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxVoiceModelFile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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

    System.out.println("=== 直接JNA VoiceModelFile テスト開始 ===");
    System.out.println("VVMファイルパス: " + vvmPath);
    System.out.println("ファイル存在確認: " + java.nio.file.Files.exists(vvmPath));

    // VoicevoxVoiceModelFileを開くための出力参照を準備
    PointerByReference outModel = new PointerByReference();
    System.out.println("✓ PointerByReference準備完了");

    // 音声モデルファイルを開く
    int result = core.voicevox_voice_model_file_open(vvmPath.toString(), outModel);
    System.out.println("voicevox_voice_model_file_open結果コード: " + result);

    // 正常に開けることを確認
    Truth.assertThat(result).isEqualTo(VoicevoxResultCode.VOICEVOX_RESULT_OK);
    Truth.assertThat(outModel.getValue()).isNotNull();
    System.out.println("✓ VVMファイル正常オープン確認");

    // VoicevoxVoiceModelFileオブジェクトを作成
    VoicevoxVoiceModelFile model = new VoicevoxVoiceModelFile(com.sun.jna.Pointer.nativeValue(outModel.getValue()));
    System.out.println("✓ VoicevoxVoiceModelFileオブジェクト作成成功");
    System.out.println("ネイティブポインタ値: 0x" + Long.toHexString(com.sun.jna.Pointer.nativeValue(outModel.getValue())));

    // モデルIDを取得してテスト
    byte[] modelId = new byte[16];
    core.voicevox_voice_model_file_id(model, modelId);
    System.out.println("✓ モデルID取得完了");

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
    System.out.println();

    // メタデータJSONを取得してテスト
    com.sun.jna.Pointer metasJson = core.voicevox_voice_model_file_create_metas_json(model);
    Truth.assertThat(metasJson).isNotNull();
    System.out.println("✓ メタデータJSONポインタ取得成功");

    String metasJsonString = metasJson.getString(0, "UTF-8");
    Truth.assertThat(metasJsonString).isNotEmpty();
    Truth.assertThat(metasJsonString).contains("styles");
    System.out.println("✓ JSONパース成功 - サイズ: " + metasJsonString.length() + " 文字");
    System.out.println("JSON内容（先頭300文字）:");
    System.out.println(metasJsonString.length() > 300 ?
        metasJsonString.substring(0, 300) + "..." : metasJsonString);

    // メモリを解放
    core.voicevox_json_free(metasJson);
    System.out.println("✓ JSONメモリ解放完了");

    core.voicevox_voice_model_file_delete(model);
    System.out.println("✓ VoiceModelFileメモリ解放完了");
    System.out.println("=== 直接JNA VoiceModelFile テスト完了 ===\n");
  }
}
