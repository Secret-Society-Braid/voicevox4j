package org.braid.society.secret.voicevox4j.struct.enumeration;

/**
 * User dictionary word type enumeration.
 */
public enum VoicevoxUserDictWordType {
  /**
   * Proper nouns.
   */
  PROPER_NOUN,
  /**
   * Common nouns.
   */
  COMMON_NOUN,
  /**
   * Verbs.
   */
  VERB,
  /**
   * Adjectives.
   */
  ADJECTIVE,
  /**
   * Suffixes.
   */
  SUFFIX;

  public int getValue() {
    return this.ordinal();
  }

  public static VoicevoxUserDictWordType fromValue(int value) {
    if (value < 0 || value >= values().length) {
      throw new IllegalArgumentException("Invalid value for VoicevoxUserDictWordType: " + value);
    }
    return values()[value];
  }
}
