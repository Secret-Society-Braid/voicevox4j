package org.braid.society.secret.voicevox4j.struct;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import org.braid.society.secret.voicevox4j.struct.enumeration.VoicevoxOnExistingVoiceModelId;

public class VoicevoxLoadVoiceModelOptions {

  public static final StructLayout LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("on_existing") // uint32_t on_existing
  ).withName("VoicevoxLoadVoiceModelOptions");

  private static final VarHandle ON_EXISTING_HANDLE = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("on_existing")
  );

  private final MemorySegment segment;

  public VoicevoxLoadVoiceModelOptions(MemorySegment segment) {
    this.segment = segment;
  }

  public VoicevoxLoadVoiceModelOptions(SegmentAllocator allocator) {
    this.segment = allocator.allocate(LAYOUT);
  }

  public MemorySegment getSegment() {
    return segment;
  }

  public VoicevoxOnExistingVoiceModelId getOnExisting() {
    int raw = (int) ON_EXISTING_HANDLE.get(segment, 0L);
    return VoicevoxOnExistingVoiceModelId.fromValue(raw);
  }

  public void setOnExisting(VoicevoxOnExistingVoiceModelId onExisting) {
    ON_EXISTING_HANDLE.set(segment, 0L, onExisting.getValue());
  }
}
