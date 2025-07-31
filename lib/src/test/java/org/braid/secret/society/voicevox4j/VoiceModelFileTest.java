package org.braid.secret.society.voicevox4j;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.braid.secret.society.voicevox4j.api.VoiceModelFile;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.junit.jupiter.api.Test;

public class VoiceModelFileTest {

  @Test
  void testVoiceModelFile() throws VoicevoxException, IOException {
    Path vvmPath = Paths.get("src/main/resources/voicevox_core/models/vvms/0.vvm").toAbsolutePath();
    Voicevox voicevox = new Voicevox(Path.of(""));

    System.out.println("=== VoiceModelFile テスト開始 ===");
    System.out.println("VVMファイルパス: " + vvmPath);
    System.out.println("ファイル存在確認: " + java.nio.file.Files.exists(vvmPath));
    System.out.println("ファイルサイズ: " + (java.nio.file.Files.exists(vvmPath) ?
      java.nio.file.Files.size(vvmPath) + " bytes" : "不明"));

    // try-with-resources文を使用した自動リソース管理
    try (VoiceModelFile modelFile = voicevox.useVoiceModelFile(vvmPath)) {
      System.out.println("✓ VoiceModelFileの作成成功");

      // モデルが正常に開かれることを確認
      Truth.assertThat(modelFile.isClosed()).isFalse();
      System.out.println("✓ モデルが開いた状態: " + !modelFile.isClosed());

      // モデルIDの取得
      byte[] modelId = modelFile.getModelId();
      Truth.assertThat(modelId).hasLength(16);
      System.out.println("✓ モデルID取得成功 (16バイト)");
      System.out.print("モデルID: ");
      for (int i = 0; i < modelId.length; i++) {
        System.out.printf("%02x", modelId[i] & 0xFF);
        if (i < modelId.length - 1) System.out.print("-");
      }
      System.out.println();

      // メタデータJSONの取得（メモリは内部で自動解放される）
      String metasJson = modelFile.getMetasJson();
      Truth.assertThat(metasJson).isNotEmpty();
      Truth.assertThat(metasJson).contains("styles");
      System.out.println("✓ メタデータJSON取得成功");
      System.out.println("JSONサイズ: " + metasJson.length() + " 文字");
      System.out.println("JSON内容（先頭500文字）:");
      System.out.println(metasJson.length() > 500 ?
        metasJson.substring(0, 500) + "..." : metasJson);

      // 複数回呼び出しても安全
      String metasJson2 = modelFile.getMetasJson();
      Truth.assertThat(metasJson2).isEqualTo(metasJson);
      System.out.println("✓ 複数回呼び出しでも同じ結果: " + metasJson.equals(metasJson2));

    } // ここで自動的にclose()が呼ばれる
    System.out.println("✓ try-with-resourcesブロック終了 - 自動クローズ完了");

    // 明示的close()のテスト
    System.out.println("\n--- 明示的close()テスト ---");
    VoiceModelFile modelFile2 = voicevox.useVoiceModelFile(vvmPath);
    Truth.assertThat(modelFile2.isClosed()).isFalse();
    System.out.println("✓ 新しいVoiceModelFile作成: クローズ状態 = " + modelFile2.isClosed());

    modelFile2.close();
    Truth.assertThat(modelFile2.isClosed()).isTrue();
    System.out.println("✓ 明示的close()実行後: クローズ状態 = " + modelFile2.isClosed());

    // クローズ後の操作は例外を投げる
    System.out.println("--- クローズ後操作テスト ---");
    try {
      modelFile2.getModelId();
      Truth.assertWithMessage("Should throw IllegalStateException").fail();
    } catch (IllegalStateException e) {
      Truth.assertThat(e.getMessage()).contains("already closed");
      System.out.println("✓ クローズ後操作で期待通りの例外発生: " + e.getMessage());
    }

    // 複数回close()を呼んでも安全
    modelFile2.close(); // 例外は投げられない
    System.out.println("✓ 複数回close()呼び出し - 例外なし");

    System.out.println("=== VoiceModelFile テスト完了 ===\n");
  }
}
