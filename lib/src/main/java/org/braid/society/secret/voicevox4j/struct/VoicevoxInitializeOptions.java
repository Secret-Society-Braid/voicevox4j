package org.braid.society.secret.voicevox4j.struct;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import org.braid.society.secret.voicevox4j.struct.enumeration.VoicevoxAccelerationMode;

public class VoicevoxInitializeOptions {
  public static final StructLayout LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("acceleration_mode"), // 0: acceleration_mode uint32_t
    ValueLayout.JAVA_SHORT.withName("cpu_num_threads"), // 4: cpu_num_threads uint16_t
    MemoryLayout.paddingLayout(2) // 6: padding for 8 byte alignment
  ).withName("VoicevoxInitializeOptions");

  private static final VarHandle ACCELERATION_MODE_HANDLE = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("acceleration_mode")
  );
  private static final VarHandle CPU_NUM_THREADS_HANDLE = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("cpu_num_threads")
  );

  private final MemorySegment segment;

  public VoicevoxInitializeOptions(MemorySegment segment) {
    this.segment = segment;
  }

  public VoicevoxInitializeOptions(SegmentAllocator allocator) {
    this.segment = allocator.allocate(LAYOUT);
  }

  public VoicevoxAccelerationMode getAccelerationMode() {
    int raw = (int) ACCELERATION_MODE_HANDLE.get(segment, 0L);
    return VoicevoxAccelerationMode.fromValue(raw);
  }

  public void setAccelerationMode(VoicevoxAccelerationMode mode) {
    ACCELERATION_MODE_HANDLE.set(segment, 0L, mode.getValue());
  }

  public short getCpuNumThreads() {
    return (short) CPU_NUM_THREADS_HANDLE.get(segment, 0L);
  }

  public void setCpuNumThreads(short threads) {
    CPU_NUM_THREADS_HANDLE.set(segment, 0L, threads);
  }

  public MemorySegment getSegment() {
    return segment;
  }
}
