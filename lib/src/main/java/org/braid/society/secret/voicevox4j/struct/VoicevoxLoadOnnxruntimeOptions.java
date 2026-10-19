package org.braid.society.secret.voicevox4j.struct;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class VoicevoxLoadOnnxruntimeOptions {
  public static final StructLayout LAYOUT = MemoryLayout.structLayout(
    ValueLayout.ADDRESS.withName("filename") // const char * filename
  ).withName("VoicevoxLoadOnnxruntimeOptions");

  private static final VarHandle FILENAME_HANDLE = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("filename")
  );

  private final MemorySegment segment;

  public VoicevoxLoadOnnxruntimeOptions(SegmentAllocator allocator) {
    this.segment = allocator.allocate(LAYOUT);
  }

  public VoicevoxLoadOnnxruntimeOptions(MemorySegment segment) {
    this.segment = segment;
  }

  public MemorySegment getSegment() {
    return segment;
  }

  public String getFilename() {
    MemorySegment ptr = (MemorySegment) FILENAME_HANDLE.get(segment, 0L);
    if(ptr.equals(MemorySegment.NULL)) {
      return null;
    }
    return ptr.reinterpret(Long.MAX_VALUE).getString(0);
  }

  public void setFilename(String filename, SegmentAllocator allocator) {
    MemorySegment ptr = MemorySegment.NULL;
    if (filename != null) {
      ptr = allocator.allocateFrom(filename);
    }
    FILENAME_HANDLE.set(segment, 0L, ptr);
  }

  public void setFilename(MemorySegment cStringPtr) {
    FILENAME_HANDLE.set(segment, 0L, cStringPtr == null ? MemorySegment.NULL : cStringPtr);
  }
}
