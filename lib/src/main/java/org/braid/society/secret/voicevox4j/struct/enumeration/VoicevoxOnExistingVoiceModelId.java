package org.braid.society.secret.voicevox4j.struct.enumeration;

/**
 * Determines the behavior when the same `VoicevoxVoiceModelFile` ID is already loaded on executing `voicevox_synthesizer_load_voice_model`.
 */
public enum VoicevoxOnExistingVoiceModelId {

  /**
   * Make it an error.
   * This is the default behavior.
   */
  ERROR(0),
  /**
   * Reload the voice model.
   * In VOICEVOX CORE, CPU/GPU memory is largely consumed by the voice model when longer sentences are synthesized,
   * so reloading the voice model is a way to reduce memory consumption.
   */
  RELOAD(1),
  /**
   * Do nothing.
   */
  SKIP(2);

  private final int value;

  VoicevoxOnExistingVoiceModelId(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static VoicevoxOnExistingVoiceModelId fromValue(int value) {
    for (VoicevoxOnExistingVoiceModelId id : values()) {
      if (id.value == value) {
        return id;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
