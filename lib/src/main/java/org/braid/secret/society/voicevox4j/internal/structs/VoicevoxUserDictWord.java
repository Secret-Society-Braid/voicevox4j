package org.braid.secret.society.voicevox4j.internal.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import java.nio.charset.StandardCharsets;

@Structure.FieldOrder({"surface", "pronunciation", "accent_type", "word_type", "priority"})
public class VoicevoxUserDictWord extends Structure {
  public String surface;
  public String pronunciation;
  public long accent_type; // uintptr_t
  public int word_type; // VoicevoxUserDictWordType
  public int priority; // uint32_t

  public VoicevoxUserDictWord() {
    super();
    // UTF-8エンコーディングを強制
    setStringEncoding(StandardCharsets.UTF_8.name());
  }

  public VoicevoxUserDictWord(Pointer p) {
    super(p);
    // UTF-8エンコーディングを強制
    setStringEncoding(StandardCharsets.UTF_8.name());
    read();
  }

  public static class ByReference extends VoicevoxUserDictWord implements Structure.ByReference {
    public ByReference() {
      super();
    }
  }
  
  public static class ByValue extends VoicevoxUserDictWord implements Structure.ByValue {
    public ByValue() {
      super();
    }
  }
}
