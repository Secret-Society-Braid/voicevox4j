package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"acceleration_mode", "cpu_num_threads"})
public class VoicevoxInitializeOptions extends Structure {
  public int acceleration_mode;
  public short cpu_num_threads;

  public VoicevoxInitializeOptions() {
    super();
  }

  public VoicevoxInitializeOptions(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends VoicevoxInitializeOptions implements Structure.ByReference {}
  public static class ByValue extends VoicevoxInitializeOptions implements Structure.ByValue {}
}
