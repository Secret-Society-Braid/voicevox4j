package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.ptr.IntByReference;

public class VoicevoxAccelerationMode extends IntByReference {
  public static final int VOICEVOX_ACCELERATION_MODE_AUTO = 0;
  public static final int VOICEVOX_ACCELERATION_MODE_CPU = 1;
  public static final int VOICEVOX_ACCELERATION_MODE_GPU = 2;

  public VoicevoxAccelerationMode() {
    super();
  }

  public VoicevoxAccelerationMode(int value) {
    super(value);
  }
}
