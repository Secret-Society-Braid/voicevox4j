package org.braid.society.secret.voicevox4j.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.braid.society.secret.voicevox4j.exception.VoicevoxException;
import org.braid.society.secret.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.structs.*;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxInitializeOptions;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxOnnxruntime;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxResultCode;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxSynthesisOptions;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxSynthesizer;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxTtsOptions;

/**
 * Voicevox音声合成器のJavaラッパークラス。
 * テキストから音声を生成するための主要な機能を提供します。
 * リソース管理を自動化し、メモリリークを防ぎます。
 */
@Slf4j
public class Synthesizer implements Closeable, AutoCloseable {

  private final VoicevoxSynthesizer nativeSynthesizer;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final Core core;

  /**
   * 音声合成器を初期化します。
   *
   * @param onnxruntime ONNXランタイム
   * @param openJtalk OpenJTalk辞書
   * @param core Coreインターフェース
   * @throws VoicevoxException 合成器の初期化に失敗した場合
   */
  public Synthesizer(VoicevoxOnnxruntime onnxruntime, OpenJTalkDictionary openJtalk, Core core) throws VoicevoxException {
    this(onnxruntime, openJtalk, core.voicevox_make_default_initialize_options(), core);
  }

  /**
   * 音声合成器を初期化します（オプション指定）。
   *
   * @param onnxruntime ONNXランタイム
   * @param openJtalk OpenJTalk辞書
   * @param options 初期化オプション
   * @param core Coreインターフェース
   * @throws VoicevoxException 合成器の初期化に失敗した場合
   */
  public Synthesizer(VoicevoxOnnxruntime onnxruntime, OpenJTalkDictionary openJtalk,
                     VoicevoxInitializeOptions options, Core core) throws VoicevoxException {
    this.core = core;
    PointerByReference outSynthesizer = new PointerByReference();
    int result = core.voicevox_synthesizer_new(onnxruntime, openJtalk.getNativeOpenJtalk(),
                                              options, outSynthesizer);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to initialize synthesizer: " + errorMessage, result);
    }

    this.nativeSynthesizer = new VoicevoxSynthesizer(Pointer.nativeValue(outSynthesizer.getValue()));
    log.debug("Synthesizer initialized successfully");
  }

  /**
   * 音声モデルを読み込みます。
   *
   * @param model 音声モデルファイル
   * @throws VoicevoxException モデルの読み込みに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void loadVoiceModel(VoiceModelFile model) throws VoicevoxException {
    ensureNotClosed();
    int result = core.voicevox_synthesizer_load_voice_model(nativeSynthesizer, model.getNativeModel());

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to load voice model: " + errorMessage, result);
    }
    log.debug("Voice model loaded successfully");
  }

  /**
   * 音声モデルをアンロードします。
   *
   * @param modelId アンロードするモデルのID
   * @throws VoicevoxException モデルのアンロードに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public void unloadVoiceModel(byte[] modelId) throws VoicevoxException {
    ensureNotClosed();
    int result = core.voicevox_synthesizer_unload_voice_model(nativeSynthesizer, modelId);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to unload voice model: " + errorMessage, result);
    }
    log.debug("Voice model unloaded successfully");
  }

  /**
   * 指定されたモデルが読み込まれているかどうかを確認します。
   *
   * @param modelId 確認するモデルのID
   * @return モデルが読み込まれている場合はtrue
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public boolean isLoadedVoiceModel(byte[] modelId) {
    ensureNotClosed();
    return core.voicevox_synthesizer_is_loaded_voice_model(nativeSynthesizer, modelId);
  }

  /**
   * GPUモードが有効かどうかを確認します。
   *
   * @return GPUモードが有効な場合はtrue
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public boolean isGpuMode() {
    ensureNotClosed();
    return core.voicevox_synthesizer_is_gpu_mode(nativeSynthesizer);
  }

  /**
   * メタデータJSONを取得します。
   *
   * @return メタデータJSON文字列
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String getMetasJson() {
    ensureNotClosed();
    Pointer metasJson = core.voicevox_synthesizer_create_metas_json(nativeSynthesizer);
    try {
      return metasJson.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(metasJson);
    }
  }

  /**
   * カナからオーディオクエリを作成します。
   *
   * @param kana カナ文字列
   * @param styleId スタイルID
   * @return オーディオクエリJSON文字列
   * @throws VoicevoxException オーディオクエリの作成に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String createAudioQueryFromKana(String kana, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAudioQueryJson = new PointerByReference();
    int result = core.voicevox_synthesizer_create_audio_query_from_kana(
        nativeSynthesizer, kana, styleId, outputAudioQueryJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to create audio query from kana: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAudioQueryJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * テキストからオーディオクエリを作成します。
   *
   * @param text テキスト
   * @param styleId スタイルID
   * @return オーディオクエリJSON文字列
   * @throws VoicevoxException オーディオクエリの作成に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String createAudioQuery(String text, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAudioQueryJson = new PointerByReference();
    int result = core.voicevox_synthesizer_create_audio_query(
        nativeSynthesizer, text, styleId, outputAudioQueryJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to create audio query: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAudioQueryJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * カナからアクセント句を作成します。
   *
   * @param kana カナ文字列
   * @param styleId スタイルID
   * @return アクセント句JSON文字列
   * @throws VoicevoxException アクセント句の作成に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String createAccentPhrasesFromKana(String kana, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAccentPhrasesJson = new PointerByReference();
    int result = core.voicevox_synthesizer_create_accent_phrases_from_kana(
        nativeSynthesizer, kana, styleId, outputAccentPhrasesJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to create accent phrases from kana: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAccentPhrasesJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * テキストからアクセント句を作成します。
   *
   * @param text テキスト
   * @param styleId スタイルID
   * @return アクセント句JSON文字列
   * @throws VoicevoxException アクセント句の作成に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String createAccentPhrases(String text, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAccentPhrasesJson = new PointerByReference();
    int result = core.voicevox_synthesizer_create_accent_phrases(
        nativeSynthesizer, text, styleId, outputAccentPhrasesJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to create accent phrases: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAccentPhrasesJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * モーラデータを置換します。
   *
   * @param accentPhrasesJson アクセント句JSON
   * @param styleId スタイルID
   * @return 置換後のアクセント句JSON文字列
   * @throws VoicevoxException モーラデータの置換に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String replaceMoraData(String accentPhrasesJson, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAccentPhrasesJson = new PointerByReference();
    int result = core.voicevox_synthesizer_replace_mora_data(
        nativeSynthesizer, accentPhrasesJson, styleId, outputAccentPhrasesJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to replace mora data: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAccentPhrasesJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * 音素長を置換します。
   *
   * @param accentPhrasesJson アクセント句JSON
   * @param styleId スタイルID
   * @return 置換後のアクセント句JSON文字列
   * @throws VoicevoxException 音素長の置換に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String replacePhonemeLength(String accentPhrasesJson, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAccentPhrasesJson = new PointerByReference();
    int result = core.voicevox_synthesizer_replace_phoneme_length(
        nativeSynthesizer, accentPhrasesJson, styleId, outputAccentPhrasesJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to replace phoneme length: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAccentPhrasesJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * モーラピッチを置換します。
   *
   * @param accentPhrasesJson アクセント句JSON
   * @param styleId スタイルID
   * @return 置換後のアクセント句JSON文字列
   * @throws VoicevoxException モーラピッチの置換に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public String replaceMoraPitch(String accentPhrasesJson, int styleId) throws VoicevoxException {
    ensureNotClosed();
    PointerByReference outputAccentPhrasesJson = new PointerByReference();
    int result = core.voicevox_synthesizer_replace_mora_pitch(
        nativeSynthesizer, accentPhrasesJson, styleId, outputAccentPhrasesJson);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to replace mora pitch: " + errorMessage, result);
    }

    Pointer jsonPointer = outputAccentPhrasesJson.getValue();
    try {
      return jsonPointer.getString(0, "UTF-8");
    } finally {
      core.voicevox_json_free(jsonPointer);
    }
  }

  /**
   * 音声を合成します（デフォルトオプション）。
   *
   * @param audioQueryJson オーディオクエリJSON
   * @param styleId スタイルID
   * @return 音声データ（WAVファイル形式）
   * @throws VoicevoxException 音声合成に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] synthesis(String audioQueryJson, int styleId) throws VoicevoxException {
    return synthesis(audioQueryJson, styleId, core.voicevox_make_default_synthesis_options());
  }

  /**
   * 音声を合成します（オプション指定）。
   *
   * @param audioQueryJson オーディオクエリJSON
   * @param styleId スタイルID
   * @param options 合成オプション
   * @return 音声データ（WAVファイル形式）
   * @throws VoicevoxException 音声合成に失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] synthesis(String audioQueryJson, int styleId, VoicevoxSynthesisOptions options) throws VoicevoxException {
    ensureNotClosed();
    IntByReference outputWavLength = new IntByReference();
    PointerByReference outputWav = new PointerByReference();

    int result = core.voicevox_synthesizer_synthesis(
        nativeSynthesizer, audioQueryJson, styleId, options, outputWavLength, outputWav);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to synthesize audio: " + errorMessage, result);
    }

    Pointer wavPointer = outputWav.getValue();
    int length = outputWavLength.getValue();

    try {
      return wavPointer.getByteArray(0, length);
    } finally {
      core.voicevox_wav_free(wavPointer);
    }
  }

  /**
   * カナからテキスト読み上げを行います（デフォルトオプション）。
   *
   * @param kana カナ文字列
   * @param styleId スタイルID
   * @return 音声データ（WAVファイル形式）
   * @throws VoicevoxException テキスト読み上げに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] ttsFromKana(String kana, int styleId) throws VoicevoxException {
    return ttsFromKana(kana, styleId, core.voicevox_make_default_tts_options());
  }

  /**
   * カナからテキスト読み上げを行います（オプション指定）。
   *
   * @param kana カナ文字列
   * @param styleId スタイルID
   * @param options TTSオプション
   * @return 音声データ（WAVファイル形式）
   * @throws VoicevoxException テキスト読み上げに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] ttsFromKana(String kana, int styleId, VoicevoxTtsOptions options) throws VoicevoxException {
    ensureNotClosed();
    IntByReference outputWavLength = new IntByReference();
    PointerByReference outputWav = new PointerByReference();

    int result = core.voicevox_synthesizer_tts_from_kana(
        nativeSynthesizer, kana, styleId, options, outputWavLength, outputWav);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to perform TTS from kana: " + errorMessage, result);
    }

    Pointer wavPointer = outputWav.getValue();
    int length = outputWavLength.getValue();

    try {
      return wavPointer.getByteArray(0, length);
    } finally {
      core.voicevox_wav_free(wavPointer);
    }
  }

  /**
   * テキストからテキスト読み上げを行います（デフォルトオプション）。
   *
   * @param text テキスト
   * @param styleId スタイルID
   * @return 音声データ（WAVファイル形式）
   * @throws VoicevoxException テキスト読み上げに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] tts(String text, int styleId) throws VoicevoxException {
    return tts(text, styleId, core.voicevox_make_default_tts_options());
  }

  /**
   * テキストからテキスト読み上げを行います（オプション指定）。
   *
   * @param text テキスト
   * @param styleId スタイルID
   * @param options TTSオプション
   * @return 音声データ（WAVファイル形式）
   * @throws VoicevoxException テキスト読み上げに失敗した場合
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public byte[] tts(String text, int styleId, VoicevoxTtsOptions options) throws VoicevoxException {
    ensureNotClosed();
    IntByReference outputWavLength = new IntByReference();
    PointerByReference outputWav = new PointerByReference();

    int result = core.voicevox_synthesizer_tts(
        nativeSynthesizer, text, styleId, options, outputWavLength, outputWav);

    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to perform TTS: " + errorMessage, result);
    }

    Pointer wavPointer = outputWav.getValue();
    int length = outputWavLength.getValue();

    try {
      return wavPointer.getByteArray(0, length);
    } finally {
      core.voicevox_wav_free(wavPointer);
    }
  }

  /**
   * 使用中のONNXランタイムを取得します。
   *
   * @return ONNXランタイム
   * @throws IllegalStateException このオブジェクトが既にクローズされている場合
   */
  public VoicevoxOnnxruntime getOnnxruntime() {
    ensureNotClosed();
    return core.voicevox_synthesizer_get_onnxruntime(nativeSynthesizer);
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
      throw new IllegalStateException("Synthesizer is already closed");
    }
  }

  /**
   * リソースを解放します。
   * このメソッドは複数回呼び出しても安全です。
   */
  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      core.voicevox_synthesizer_delete(nativeSynthesizer);
      log.debug("Synthesizer closed and resources released");
    }
  }
}
