package org.braid.society.secret.voicevox4j.struct;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class VoicevoxAudioQueryFrameLengthOptions {

  public static final StructLayout LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_BOOLEAN.withName("enable_interrogative_upspeak") // bool enable_interrogative_upspeak
  ).withName("VoicevoxAudioQueryFrameLengthOptions");

  private static final VarHandle ENABLE_INTERROGATIVE_UPSPEAK_HANDLE = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("enable_interrogative_upspeak")
  );

  private final MemorySegment segment;

  public VoicevoxAudioQueryFrameLengthOptions(SegmentAllocator allocator) {
    this.segment = allocator.allocate(LAYOUT);
  }

  public VoicevoxAudioQueryFrameLengthOptions(MemorySegment segment) {
    this.segment = segment;
  }

  public MemorySegment getSegment() {
    return segment;
  }

  public boolean isEnableInterrogativeUpspeak() {
    return (boolean) ENABLE_INTERROGATIVE_UPSPEAK_HANDLE.get(segment, 0L);
  }

  public void setEnableInterrogativeUpspeakHandle(boolean enable) {
    ENABLE_INTERROGATIVE_UPSPEAK_HANDLE.set(segment, 0L, enable);
  }
}
