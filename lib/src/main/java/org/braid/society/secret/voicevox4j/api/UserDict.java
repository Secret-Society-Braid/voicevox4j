package org.braid.society.secret.voicevox4j.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.braid.society.secret.voicevox4j.exception.VoicevoxException;
import org.braid.society.secret.voicevox4j.internal.Core;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxResultCode;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxUserDict;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxUserDictWord;

/**
 * ユーザー辞書のJavaラッパークラス。
 * リソース管理を自動化し、メモリリークを防ぎます。
 */
public class UserDict implements Closeable, AutoCloseable {

  private final VoicevoxUserDict nativeUserDict;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final Core core;

  /**
   * 新しいユーザー辞書を作成します。
   *
   * @param core Coreインターフェース
   * @throws IllegalStateException ユーザー辞書の作成に失敗した場合
   */
  public UserDict(Core core) {
    this.core = core;
    try {
      Pointer pointer = core.voicevox_user_dict_new();
      this.nativeUserDict = new VoicevoxUserDict(Pointer.nativeValue(pointer));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create user dictionary: " + e.getMessage(), e);
    }
  }

  /**
   * 指定されたパスからユーザー辞書を読み込みます。
   *
   * @param dictPath 辞書ファイルのパス
   * @throws VoicevoxException 辞書の読み込みに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void load(Path dictPath) throws VoicevoxException {
    ensureNotClosed();
    int result = core.voicevox_user_dict_load(nativeUserDict, dictPath.toString());

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to load user dictionary: " + errorMessage, result);
    }
  }

  /**
   * ユーザー辞書に新しい単語を追加します。
   *
   * @param surface 表記
   * @param pronunciation 読み方
   * @param accentType アクセント型
   * @return 追加された単語のUUID
   * @throws VoicevoxException 単語の追加に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public UUID addWord(String surface, String pronunciation, long accentType) throws VoicevoxException {
    ensureNotClosed();

    // VoicevoxUserDictWord構造体を作成
    VoicevoxUserDictWord.ByValue word = new VoicevoxUserDictWord.ByValue();
    word.surface = surface;
    word.pronunciation = pronunciation;
    word.accent_type = accentType;
    word.word_type = 0; // デフォルト値
    word.priority = 5;  // デフォルト値

    byte[] outputWordUuid = new byte[16];

    int result = core.voicevox_user_dict_add_word(nativeUserDict, word, outputWordUuid);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to add word to user dictionary: " + errorMessage, result);
    }

    return uuidFromBytes(outputWordUuid);
  }

  /**
   * ユーザー辞書の単語を更新します。
   *
   * @param wordUuid 更新する単語のUUID
   * @param surface 新しい表記
   * @param pronunciation 新しい読み方
   * @param accentType 新しいアクセント型
   * @throws VoicevoxException 単語の更新に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void updateWord(UUID wordUuid, String surface, String pronunciation, long accentType) throws VoicevoxException {
    ensureNotClosed();

    // VoicevoxUserDictWord構造体を作成
    VoicevoxUserDictWord.ByValue word = new VoicevoxUserDictWord.ByValue();
    word.surface = surface;
    word.pronunciation = pronunciation;
    word.accent_type = accentType;
    word.word_type = 0; // デフォルト値
    word.priority = 5;  // デフォルト値

    byte[] uuidBytes = uuidToBytes(wordUuid);

    int result = core.voicevox_user_dict_update_word(nativeUserDict, uuidBytes, word);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to update word in user dictionary: " + errorMessage, result);
    }
  }

  /**
   * ユーザー辞書から単語を削除します。
   *
   * @param wordUuid 削除する単語のUUID
   * @throws VoicevoxException 単語の削除に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void removeWord(UUID wordUuid) throws VoicevoxException {
    ensureNotClosed();
    byte[] uuidBytes = uuidToBytes(wordUuid);

    int result = core.voicevox_user_dict_remove_word(nativeUserDict, uuidBytes);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to remove word from user dictionary: " + errorMessage, result);
    }
  }

  /**
   * ユーザー辞書をJSON形式で出力します。
   *
   * @return ユーザー辞書のJSON文字列
   * @throws VoicevoxException JSON変換に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String toJson() throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputJson = new PointerByReference();
    int result = core.voicevox_user_dict_to_json(nativeUserDict, outputJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to convert user dictionary to JSON: " + errorMessage, result);
    }

    Pointer jsonPointer = outputJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      // JSONメモリは即座に解放
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * 他のユーザー辞書をこの辞書にインポートします。
   *
   * @param otherDict インポートする辞書
   * @throws VoicevoxException インポートに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void importFrom(UserDict otherDict) throws VoicevoxException {
    ensureNotClosed();
    otherDict.ensureNotClosed();

    int result = core.voicevox_user_dict_import(nativeUserDict, otherDict.nativeUserDict);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to import user dictionary: " + errorMessage, result);
    }
  }

  /**
   * ユーザー辞書を指定されたパスに保存します。
   *
   * @param path 保存先パス
   * @throws VoicevoxException 保存に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void save(Path path) throws VoicevoxException {
    ensureNotClosed();
    int result = core.voicevox_user_dict_save(nativeUserDict, path.toString());

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to save user dictionary: " + errorMessage, result);
    }
  }

  /**
   * 内部使用のためのネイティブVoicevoxUserDictオブジェクトを取得します。
   *
   * @return ネイティブVoicevoxUserDictオブジェクト
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public VoicevoxUserDict getNativeUserDict() {
    ensureNotClosed();
    return nativeUserDict;
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
      throw new IllegalStateException("UserDict is already closed");
    }
  }

  private UUID uuidFromBytes(byte[] bytes) {
    if (bytes.length != 16) {
      throw new IllegalArgumentException("UUID bytes must be 16 bytes long");
    }

    long mostSigBits = 0;
    long leastSigBits = 0;

    for (int i = 0; i < 8; i++) {
      mostSigBits = (mostSigBits << 8) | (bytes[i] & 0xff);
    }

    for (int i = 8; i < 16; i++) {
      leastSigBits = (leastSigBits << 8) | (bytes[i] & 0xff);
    }

    return new UUID(mostSigBits, leastSigBits);
  }

  private byte[] uuidToBytes(UUID uuid) {
    byte[] bytes = new byte[16];
    long mostSigBits = uuid.getMostSignificantBits();
    long leastSigBits = uuid.getLeastSignificantBits();

    for (int i = 0; i < 8; i++) {
      bytes[i] = (byte) (mostSigBits >>> (8 * (7 - i)));
    }

    for (int i = 8; i < 16; i++) {
      bytes[i] = (byte) (leastSigBits >>> (8 * (15 - i)));
    }

    return bytes;
  }

  /**
   * リソースを解放します。
   * このメソッドは複数回呼び出しても安全です。
   */
  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      core.voicevox_user_dict_delete(nativeUserDict);
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
        System.err.println("Warning: UserDict was not explicitly closed. " +
          "Consider using try-with-resources or explicit close().");
        close();
      }
    } finally {
      super.finalize();
    }
  }
}
