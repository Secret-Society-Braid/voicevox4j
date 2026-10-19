package org.braid.society.secret.voicevox4j.struct.enumeration;

/**
 * This enum represents the compute result codes returned by the native library.
 * Referer to each enum documentation for the purpose.
 * <p>
 * Original Note: There are some result codes that are implemented only for C API and not used in Rust API.
 */
public enum VoicevoxResultCode {
  /**
   * Successful.
   */
  OK,
  /**
   * The OpenJTalk dictionary file is not loaded.
   */
  NOT_LOADED_OPENJTALK_DICT_ERROR,
  /**
   * Failed to get the list of supported devices.
   */
  GET_SUPPORTED_DEVICES_ERROR,
  /**
   * GPU mode is not supported on this device.
   */
  GPU_SUPPORT_ERROR,
  /**
   * Failed to load/initialize the inference runtime library.
   */
  INIT_INFERENCE_RUNTIME_ERROR,
  /**
   * There is no style corresponding to the specified style ID.
   */
  STYLE_NOT_FOUND_ERROR,
  /**
   * There is no voice model corresponding to the specified model ID.
   */
  MODEL_NOT_FOUND_ERROR,
  /**
   * invalid inference result, or failure.
   */
  RUN_MODEL_ERROR,
  /**
   * Failed to analyze the input text.
   */
  ANALYZE_TEXT_ERROR,
  /**
   * invalid UTF-8 input was provided to the function.
   */
  INVALID_UTF8_INPUT_ERROR,
  /**
   * Failed to parse AquesTalk-like syntax text.
   */
  PARSE_KANA_ERROR,
  /**
   * Invalid audio query was provided to the function.
   */
  INVALID_AUDIO_QUERY_ERROR,
  /**
   * Invalid accent phrase was provided to the function.
   */
  INVALID_ACCENT_PHRASE_ERROR,
  /**
   * Failed to open the zip file.
   */
  OPEN_ZIP_FILE_ERROR,
  /**
   * Failed to read the entry from the zip file.
   */
  READ_ZIP_ENTRY_ERROR,
  /**
   * Illegal format of the provided model file was detected.
   */
  INVALID_MODEL_FORMAT_ERROR,
  /**
   * Tried to load a model that is already loaded.
   */
  MODEL_ALREADY_LOADED_ERROR,
  /**
   * Tried to load a style that is already loaded.
   */
  STYLE_ALREADY_LOADED_ERROR,
  /**
   * The model data is invalid.
   */
  INVALID_MODEL_DATA_ERROR,
  /**
   * Failed to load the user dictionary.
   */
  LOAD_USER_DICT_ERROR,
  /**
   * Failed to save the user dictionary.
   */
  SAVE_USER_DICT_ERROR,
  /**
   * The specified word was not found in the user dictionary.
   */
  USER_DICT_WORD_NOT_FOUND_ERROR,
  /**
   * Unable to configure the OpenJTalk user dictionary.
   */
  USE_USER_DICT_ERROR,
  /**
   * Failed to validate the word in the user dictionary.
   */
  INVALID_USER_DICT_WORD_ERROR,
  /**
   * Failed to transform UUID.
   */
  INVALID_UUID_ERROR,
  /**
   * The specified mora is invalid.
   */
  INVALID_MORA_ERROR,
  /**
   * The specified music score is invalid.
   */
  INVALID_SCORE_ERROR,
  /**
   * The specified music note is invalid.
   */
  INVALID_NOTE_ERROR,
  /**
   * The specified audio query is invalid.
   */
  INVALID_FRAME_AUDIO_QUERY_ERROR,
  /**
   * The specified phoneme is invalid.
   */
  INVALID_FRAME_PHONEME_ERROR,
  /**
   * The incompatible/illegal combination of music score and FrameAudioQuery was provided to the function.
   */
  INCOMPATIBLE_QUERIES_ERROR;

  public int getCode() {
    return this.ordinal();
  }

  public String getMessage() {
    return this.name();
  }

  public static VoicevoxResultCode fromCode(int code) {
    if (code < 0 || code >= values().length) {
      throw new IllegalArgumentException("Invalid code: " + code);
    }
    return values()[code];
  }
}
