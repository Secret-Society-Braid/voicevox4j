package org.braid.secret.society.voicevox4j.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.braid.secret.society.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxResultCode;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxVoiceModelFile;

/**
 * VoiceVox音声モデルファイルのJavaラッパークラス。
 * リソース管理を自動化し、メモリリークを防ぎます。
 */
public class VoiceModelFile implements Closeable, AutoCloseable {

  private final VoicevoxVoiceModelFile nativeModel;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final Core core;

  /**
   * 音声モデルファイルを開きます。
   *
   * @param path VVMファイルのパス
   * @throws VoicevoxException ファイルの読み込みに失敗した場合
   */
  public VoiceModelFile(Path path, Core core) throws VoicevoxException {
    this.core = core;
    PointerByReference outModel = new PointerByReference();
    int result = core.voicevox_voice_model_file_open(path.toString(), outModel);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to open voice model file: " + errorMessage, result);
    }

    this.nativeModel = new VoicevoxVoiceModelFile(Pointer.nativeValue(outModel.getValue()));
  }

  /**
   * モデルIDを取得します。
   *
   * @return 16バイトのモデルID
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] getModelId() {
    ensureNotClosed();
    byte[] modelId = new byte[16];
    core.voicevox_voice_model_file_id(nativeModel, modelId);
    return modelId;
  }

  /**
   * メタデータJSONを取得します。
   *
   * @return メタデータJSON文字列
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String getMetasJson() {
    ensureNotClosed();
    Pointer metasJson = core.voicevox_voice_model_file_create_metas_json(nativeModel);
    try {
      return metasJson.getString(0, "UTF-8");
    } finally {
      // JSONメモリは即座に解放
      core.voicevox_json_free(metasJson);
    }
  }

  /**
   * 内部使用のためのネイティブモデルオブジェクトを取得します。
   *
   * @return ネイティブVoicevoxVoiceModelFileオブジェクト
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public VoicevoxVoiceModelFile getNativeModel() {
    ensureNotClosed();
    return nativeModel;
  }

  /**
   * このオブジェクトがクローズされているかどうかを確認します。
   *
   * @return クローズされている場合はtrue
   */
  public boolean isClosed() {
    return closed.get();
  }

  private void ensureNotClosed() {
    if (closed.get()) {
      throw new IllegalStateException("VoiceModelFile is already closed");
    }
  }

  /**
   * リソースを解放します。
   * このメソッドは複数回呼び出しても安全です。
   */
  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      core.voicevox_voice_model_file_delete(nativeModel);
    }
  }

  /**
   * ファイナライザーでリソースの解放を保証します。
   * ただし、明示的なclose()呼び出しが推奨されます。
   */
  @SuppressWarnings("removal")
  @Override
  protected void finalize() throws Throwable {
    try {
      if (!closed.get()) {
        System.err.println("Warning: VoiceModelFile was not explicitly closed. " +
          "Consider using try-with-resources or explicit close().");
        close();
      }
    } finally {
      super.finalize();
    }
  }
}
