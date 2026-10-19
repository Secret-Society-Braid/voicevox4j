package org.braid.society.secret.voicevox4j.struct.enumeration;

/**
 * Determines which acceleration mode to use when synthesizing speech.
 */
public enum VoicevoxAccelerationMode {

  /**
   * Automatically select the best acceleration mode based on the system's capabilities.
   */
  AUTO(0),
  /**
   * Set the acceleration mode to CPU.
   */
  CPU(1),
  /**
   * Set the acceleration mode to GPU.
   */
  GPU(2);

  private final int value;

  VoicevoxAccelerationMode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static VoicevoxAccelerationMode fromValue(int value) {
    for (VoicevoxAccelerationMode mode : values()) {
      if (mode.value == value) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
