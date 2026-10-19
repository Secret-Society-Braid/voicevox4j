package org.braid.society.secret.voicevox4j.api;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Meta {
  private static final Logger log = LoggerFactory.getLogger(Meta.class);
  private Linker linker;
  private SymbolLookup lookup;

  public Meta(Linker linker, SymbolLookup lookup) {
    this.linker = linker;
    this.lookup = lookup;
  }

  public String getCoreVersion() {
    MethodHandle getCoreVersionHandle = this.linker.downcallHandle(
      this.lookup.find("voicevox_get_version").orElseThrow(() -> new RuntimeException("Failed to find symbol")),
      FunctionDescriptor.of(ValueLayout.ADDRESS)
    );

    String version = null;

    MemorySegment versionPtr = null;
    try {
      versionPtr = (MemorySegment) getCoreVersionHandle.invokeExact();
      version = versionPtr.reinterpret(Long.MAX_VALUE).getString(0);
      log.debug("voicevox_get_version returned: {}", version);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

    return version;
  }
}
