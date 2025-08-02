package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.Pointer;

public class VoicevoxVoiceModelFile extends Pointer {

  /**
   * Create from native pointer.  Don't use this unless you know what you're doing.
   *
   * @param peer
   */
  public VoicevoxVoiceModelFile(long peer) {
    super(peer);
  }
}
