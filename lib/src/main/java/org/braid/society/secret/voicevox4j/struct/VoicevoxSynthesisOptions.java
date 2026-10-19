package org.braid.society.secret.voicevox4j.struct;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class VoicevoxSynthesisOptions {

  public static final StructLayout LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BOOLEAN.withName("enable_interrogative_upspeak") // bool enable_interrogative_upspeak
  );

  public static final VarHandle ENABLE_INTERROGATIVE_UPSPEAK_HANDLE = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("enable_interrogative_upspeak")
  );

  public MemorySegment segment;

  public VoicevoxSynthesisOptions(MemorySegment segment) {
    this.segment = segment;
  }

  public MemorySegment getSegment() {
    return segment;
  }

  public boolean getEnableInterrogativeUpspeak() {
    return (boolean) ENABLE_INTERROGATIVE_UPSPEAK_HANDLE.get(segment, 0L);
  }

  public void setEnableInterrogativeUpspeak(boolean value) {
    ENABLE_INTERROGATIVE_UPSPEAK_HANDLE.set(segment, value);
  }
}
