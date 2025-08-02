package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.ptr.IntByReference;

public class VoicevoxUserDictWordType extends IntByReference {
  public static final int VOICEVOX_USER_DICT_WORD_TYPE_PROPER_NOUN = 0;
  public static final int VOICEVOX_USER_DICT_WORD_TYPE_COMMON_NOUN = 1;
  public static final int VOICEVOX_USER_DICT_WORD_TYPE_VERB = 2;
  public static final int VOICEVOX_USER_DICT_WORD_TYPE_ADJECTIVE = 3;
  public static final int VOICEVOX_USER_DICT_WORD_TYPE_SUFFIX = 4;

  public VoicevoxUserDictWordType() {
    super();
  }

  public VoicevoxUserDictWordType(int value) {
    super(value);
  }
}
