package org.braid.secret.society.voicevox4j.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.braid.secret.society.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.structs.OpenJtalkRc;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxResultCode;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxUserDict;

/**
 * OpenJTalk辞書のJavaラッパークラス。
 * リソース管理を自動化し、メモリリークを防ぎます。
 */
public class OpenJTalkDictionary implements Closeable, AutoCloseable {

  private final OpenJtalkRc nativeOpenJtalk;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final Core core;

  /**
   * OpenJTalk辞書を初期化します。
   *
   * @param openJtalkDicDir OpenJTalk辞書ディレクトリのパス
   * @param core Coreインターフェース
   * @throws VoicevoxException 辞書の初期化に失敗した場合
   */
  public OpenJTalkDictionary(Path openJtalkDicDir, Core core) throws VoicevoxException {
    this.core = core;
    PointerByReference outOpenJtalk = new PointerByReference();
    int result = core.voicevox_open_jtalk_rc_new(openJtalkDicDir.toString(), outOpenJtalk);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to initialize OpenJTalk dictionary: " + errorMessage, result);
    }

    this.nativeOpenJtalk = new OpenJtalkRc(Pointer.nativeValue(outOpenJtalk.getValue()));
  }

  /**
   * ユーザー辞書を使用するように設定します。
   *
   * @param userDict 使用するユーザー辞書
   * @throws VoicevoxException ユーザー辞書の設定に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void useUserDict(VoicevoxUserDict userDict) throws VoicevoxException {
    ensureNotClosed();
    int result = core.voicevox_open_jtalk_rc_use_user_dict(nativeOpenJtalk, userDict);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to use user dictionary: " + errorMessage, result);
    }
  }

  /**
   * テキストを解析してアクセント句のJSONを作成します。
   *
   * @param text 解析するテキスト
   * @return アクセント句のJSON文字列
   * @throws VoicevoxException テキストの解析に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String analyze(String text) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAccentPhrasesJson = new PointerByReference();
    int result = core.voicevox_open_jtalk_rc_analyze(nativeOpenJtalk, text, outputAccentPhrasesJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to analyze text: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAccentPhrasesJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      // JSONメモリは即座に解放
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * 内部使用のためのネイティブOpenJtalkRcオブジェクトを取得します。
   *
   * @return ネイティブOpenJtalkRcオブジェクト
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public OpenJtalkRc getNativeOpenJtalk() {
    ensureNotClosed();
    return nativeOpenJtalk;
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
      throw new IllegalStateException("OpenJTalkDictionary is already closed");
    }
  }

  /**
   * リソースを解放します。
   * このメソッドは複数回呼び出しても安全です。
   */
  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      core.voicevox_open_jtalk_rc_delete(nativeOpenJtalk);
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
        System.err.println("Warning: OpenJTalkDictionary was not explicitly closed. " +
          "Consider using try-with-resources or explicit close().");
        close();
      }
    } finally {
      super.finalize();
    }
  }
}
