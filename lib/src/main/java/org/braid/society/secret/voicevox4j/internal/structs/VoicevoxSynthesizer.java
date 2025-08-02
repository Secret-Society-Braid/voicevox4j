package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.Pointer;

public class VoicevoxSynthesizer extends Pointer {

  /**
   * Create from native pointer.  Don't use this unless you know what you're doing.
   *
   * @param peer
   */
  public VoicevoxSynthesizer(long peer) {
    super(peer);
  }
}
