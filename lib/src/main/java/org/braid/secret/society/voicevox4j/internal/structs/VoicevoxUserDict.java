package org.braid.secret.society.voicevox4j.internal.structs;

import com.sun.jna.Pointer;

public class VoicevoxUserDict extends Pointer {

  /**
   * Create from native pointer.  Don't use this unless you know what you're doing.
   *
   * @param peer
   */
  public VoicevoxUserDict(long peer) {
    super(peer);
  }
}
