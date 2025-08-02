package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"enable_interrogative_upspeak"})
public class VoicevoxTtsOptions extends Structure {

  public VoicevoxTtsOptions() {
    super();
  }

  public VoicevoxTtsOptions(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends VoicevoxTtsOptions implements Structure.ByReference {}
  public static class ByValue extends VoicevoxTtsOptions implements Structure.ByValue {}
}
