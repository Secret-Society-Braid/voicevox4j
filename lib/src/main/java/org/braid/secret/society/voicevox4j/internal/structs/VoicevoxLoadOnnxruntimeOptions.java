package org.braid.secret.society.voicevox4j.internal.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"filename"})
public class VoicevoxLoadOnnxruntimeOptions extends Structure {
  public String filename;

  public VoicevoxLoadOnnxruntimeOptions() {
    super();
  }

  public VoicevoxLoadOnnxruntimeOptions(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends VoicevoxLoadOnnxruntimeOptions implements Structure.ByReference {}
  public static class ByValue extends VoicevoxLoadOnnxruntimeOptions implements Structure.ByValue {}
}
