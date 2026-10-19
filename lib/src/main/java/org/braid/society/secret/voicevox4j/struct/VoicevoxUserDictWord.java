package org.braid.society.secret.voicevox4j.struct;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import org.braid.society.secret.voicevox4j.struct.enumeration.VoicevoxUserDictWordType;

public class VoicevoxUserDictWord {

  public static final StructLayout LAYOUT = MemoryLayout.structLayout(
    ValueLayout.ADDRESS.withName("surface"),
    ValueLayout.ADDRESS.withName("pronunciation"),
    ValueLayout.JAVA_LONG.withName("accent_type"),
    ValueLayout.JAVA_INT.withName("word_type"),
    ValueLayout.JAVA_BYTE.withName("priority"),
    MemoryLayout.paddingLayout(3)
  );

  public static final VarHandle SURFACE_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("surface"));
  public static final VarHandle PRONUNCIATION_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("pronunciation"));
  public static final VarHandle ACCENT_TYPE_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("accent_type"));
  public static final VarHandle WORD_TYPE_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("word_type"));
  public static final VarHandle PRIORITY_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("priority"));

  private MemorySegment segment;

  public VoicevoxUserDictWord(MemorySegment segment) {
    this.segment = segment;
  }

  public VoicevoxUserDictWord(SegmentAllocator allocator) {
    this.segment = allocator.allocate(LAYOUT);
  }

  public MemorySegment getSegment() {
    return segment;
  }

  public String getSurface() {
    MemorySegment ptr = (MemorySegment) SURFACE_HANDLE.get(segment, 0L);
    if(ptr == null) {
      return null;
    }
    return ptr.reinterpret(Long.MAX_VALUE).getString(0);
  }

  public void setSurface(String surface, SegmentAllocator allocator) {
    MemorySegment ptr = MemorySegment.NULL;
    if (surface != null) {
      ptr = allocator.allocateFrom(surface);
    }
    SURFACE_HANDLE.set(segment, 0L, ptr);
  }

  public void setSurface(MemorySegment cStringPtr) {
    SURFACE_HANDLE.set(segment, 0L, cStringPtr == null ? MemorySegment.NULL : cStringPtr);
  }

  public String getPronunciation() {
    MemorySegment ptr = (MemorySegment) PRONUNCIATION_HANDLE.get(segment, 0L);
    if(ptr == null) {
      return null;
    }
    return ptr.reinterpret(Long.MAX_VALUE).getString(0);
  }

  public void setPronunciation(String pronunciation, SegmentAllocator allocator) {
    MemorySegment ptr = MemorySegment.NULL;
    if (pronunciation != null) {
      ptr = allocator.allocateFrom(pronunciation);
    }
    PRONUNCIATION_HANDLE.set(segment, 0L, ptr);
  }

  public void setPronunciation(MemorySegment cStringPtr) {
    PRONUNCIATION_HANDLE.set(segment, 0L, cStringPtr == null ? MemorySegment.NULL : cStringPtr);
  }

  public long getAccentType() {
    return (long) ACCENT_TYPE_HANDLE.get(segment, 0L);
  }

  public void setAccentType(long accentType) {
    ACCENT_TYPE_HANDLE.set(segment, 0L, accentType);
  }

  public VoicevoxUserDictWordType getWordType() {
    int wordTypeValue = (int) WORD_TYPE_HANDLE.get(segment, 0L);
    return VoicevoxUserDictWordType.fromValue(wordTypeValue);
  }

  public void setWordType(VoicevoxUserDictWordType wordType) {
    WORD_TYPE_HANDLE.set(segment, 0L, wordType.getValue());
  }

  public byte getPriority() {
    return (byte) PRIORITY_HANDLE.get(segment, 0L);
  }

  public void setPriority(byte priority) {
    PRIORITY_HANDLE.set(segment, 0L, priority);
  }
}
