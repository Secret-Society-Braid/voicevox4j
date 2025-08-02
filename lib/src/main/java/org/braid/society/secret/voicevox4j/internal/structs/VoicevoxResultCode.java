package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.ptr.IntByReference;

public class VoicevoxResultCode extends IntByReference {
  public static final int VOICEVOX_RESULT_OK = 0;
  public static final int VOICEVOX_RESULT_NOT_LOADED_OPENJTALK_DICT_ERROR = 1;
  public static final int VOICEVOX_RESULT_GET_SUPPORTED_DEVICES_ERROR = 3;
  public static final int VOICEVOX_RESULT_GPU_SUPPORT_ERROR = 4;
  public static final int VOICEVOX_RESULT_INIT_INFERENCE_RUNTIME_ERROR = 29;
  public static final int VOICEVOX_RESULT_STYLE_NOT_FOUND_ERROR = 6;
  public static final int VOICEVOX_RESULT_MODEL_NOT_FOUND_ERROR = 7;
  public static final int VOICEVOX_RESULT_RUN_MODEL_ERROR = 8;
  public static final int VOICEVOX_RESULT_ANALYZE_TEXT_ERROR = 11;
  public static final int VOICEVOX_RESULT_INVALID_UTF8_INPUT_ERROR = 12;
  public static final int VOICEVOX_RESULT_PARSE_KANA_ERROR = 13;
  public static final int VOICEVOX_RESULT_INVALID_AUDIO_QUERY_ERROR = 14;
  public static final int VOICEVOX_RESULT_INVALID_ACCENT_PHRASE_ERROR = 15;
  public static final int VOICEVOX_RESULT_OPEN_ZIP_FILE_ERROR = 16;
  public static final int VOICEVOX_RESULT_READ_ZIP_ENTRY_ERROR = 17;
  public static final int VOICEVOX_RESULT_INVALID_MODEL_HEADER_ERROR = 28;
  public static final int VOICEVOX_RESULT_MODEL_ALREADY_LOADED_ERROR = 18;
  public static final int VOICEVOX_RESULT_STYLE_ALREADY_LOADED_ERROR = 26;
  public static final int VOICEVOX_RESULT_INVALID_MODEL_DATA_ERROR = 27;
  public static final int VOICEVOX_RESULT_LOAD_USER_DICT_ERROR = 20;
  public static final int VOICEVOX_RESULT_SAVE_USER_DICT_ERROR = 21;
  public static final int VOICEVOX_RESULT_USER_DICT_WORD_NOT_FOUND_ERROR = 22;
  public static final int VOICEVOX_RESULT_USE_USER_DICT_ERROR = 23;
  public static final int VOICEVOX_RESULT_INVALID_USER_DICT_WORD_ERROR = 24;
  public static final int VOICEVOX_RESULT_INVALID_UUID_ERROR = 25;

  public VoicevoxResultCode() {
    super();
  }

  public VoicevoxResultCode(int value) {
    super(value);
  }
}
