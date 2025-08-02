package org.braid.society.secret.voicevox4j.internal.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.nio.charset.StandardCharsets;

@Structure.FieldOrder({"filename"})
public class VoicevoxLoadOnnxruntimeOptions extends Structure {
  public String filename;

  public VoicevoxLoadOnnxruntimeOptions() {
    super();
    setStringEncoding(StandardCharsets.UTF_8.name());
  }

  public VoicevoxLoadOnnxruntimeOptions(Pointer p) {
    super(p);
    setStringEncoding(StandardCharsets.UTF_8.name());
    read();
  }

  public static class ByReference extends VoicevoxLoadOnnxruntimeOptions implements Structure.ByReference {}
  public static class ByValue extends VoicevoxLoadOnnxruntimeOptions implements Structure.ByValue {}
}
