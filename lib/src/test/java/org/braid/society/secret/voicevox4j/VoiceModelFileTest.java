package org.braid.society.secret.voicevox4j;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.braid.society.secret.voicevox4j.api.VoiceModelFile;
import org.braid.society.secret.voicevox4j.exception.VoicevoxException;
import org.junit.jupiter.api.Test;

@Slf4j
public class VoiceModelFileTest {

  @Test
  void testVoiceModelFile() throws VoicevoxException, IOException {
    Path vvmPath = Paths.get("src/main/resources/voicevox_core/models/vvms/0.vvm").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    log.debug("=== VoiceModelFile テスト開始 ===");
    log.debug("VVMファイルパス: {}", vvmPath);
    log.debug("ファイル存在確認: {}", java.nio.file.Files.exists(vvmPath));
    log.debug("ファイルサイズ: {}", java.nio.file.Files.exists(vvmPath) ?
      java.nio.file.Files.size(vvmPath) + " bytes" : "不明");

    // try-with-resources文を使用した自動リソース管理
    try (VoiceModelFile modelFile = voicevox.useVoiceModelFile(vvmPath)) {
      log.debug("✓ VoiceModelFileの作成成功");

      // モデルが正常に開かれることを確認
      Truth.assertThat(modelFile.isClosed()).isFalse();
      log.debug("✓ モデルが開いた状態: {}", !modelFile.isClosed());

      // モデルIDの取得
      byte[] modelId = modelFile.getModelId();
      Truth.assertThat(modelId).hasLength(16);
      log.debug("✓ モデルID取得成功 (16バイト)");
      StringBuilder modelIdHex = new StringBuilder();
      for (int i = 0; i < modelId.length; i++) {
        modelIdHex.append(String.format("%02x", modelId[i] & 0xFF));
        if (i < modelId.length - 1) modelIdHex.append("-");
      }
      log.debug("モデルID: {}", modelIdHex);

      // メタデータJSONの取得（メモリは内部で自動解放される）
      String metasJson = modelFile.getMetasJson();
      Truth.assertThat(metasJson).isNotEmpty();
      Truth.assertThat(metasJson).contains("styles");
      log.debug("✓ メタデータJSON取得成功");
      log.debug("JSONサイズ: {} 文字", metasJson.length());
      log.debug("JSON内容（先頭500文字）:");
      log.debug(metasJson.length() > 500 ?
        metasJson.substring(0, 500) + "..." : metasJson);

      // 複数回呼び出しても安全
      String metasJson2 = modelFile.getMetasJson();
      Truth.assertThat(metasJson2).isEqualTo(metasJson);
      log.debug("✓ 複数回呼び出しでも同じ結果: {}", metasJson.equals(metasJson2));

    } // ここで自動的にclose()が呼ばれる
    log.debug("✓ try-with-resourcesブロック終了 - 自動クローズ完了");

    // 明示的close()のテスト
    log.debug("--- 明示的close()テスト ---");
    VoiceModelFile modelFile2 = voicevox.useVoiceModelFile(vvmPath);
    Truth.assertThat(modelFile2.isClosed()).isFalse();
    log.debug("✓ 新しいVoiceModelFile作成: クローズ状態 = {}", modelFile2.isClosed());

    modelFile2.close();
    Truth.assertThat(modelFile2.isClosed()).isTrue();
    log.debug("✓ 明示的close()実行後: クローズ状態 = {}", modelFile2.isClosed());

    // クローズ後の操作は例外を投げる
    log.debug("--- クローズ後操作テスト ---");
    try {
      modelFile2.getModelId();
      Truth.assertWithMessage("Should throw IllegalStateException").fail();
    } catch (IllegalStateException e) {
      Truth.assertThat(e.getMessage()).contains("already closed");
      log.debug("✓ クローズ後操作で期待通りの例外発生: {}", e.getMessage());
    }

    // 複数回close()を呼んでも安全
    modelFile2.close(); // 例外は投げられない
    log.debug("✓ 複数回close()呼び出し - 例外なし");

    log.debug("=== VoiceModelFile テスト完了 ===");
  }
}
