package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"enable_interrogative_upspeak"})
public class VoicevoxSynthesisOptions extends Structure {

  public VoicevoxSynthesisOptions() {
    super();
  }

  public VoicevoxSynthesisOptions(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends VoicevoxSynthesisOptions implements Structure.ByReference {}
  public static class ByValue extends VoicevoxSynthesisOptions implements Structure.ByValue {}
}
