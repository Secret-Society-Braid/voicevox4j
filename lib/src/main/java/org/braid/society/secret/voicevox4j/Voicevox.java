package org.braid.society.secret.voicevox4j;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Voicevox {

  private final Linker linker;
  private final SymbolLookup lookup;

  public Voicevox(Path path) {
    System.load(path.toString());

    this.linker = Linker.nativeLinker();
    this.lookup = SymbolLookup.loaderLookup();
  }

  public String getCoreVersion() {
    MethodHandle getCoreVersionHandle = linker.downcallHandle(
      lookup.find("voicevox_get_version").orElseThrow(() -> new RuntimeException("Failed to find symbol: voicevox_get_version")),
      FunctionDescriptor.of(ValueLayout.ADDRESS)
    );

    String version = null;

    try {
      MemorySegment versionPtr = (MemorySegment) getCoreVersionHandle.invokeExact();
      version = versionPtr.getString(0);
    } catch (Throwable e) {
      log.error("Failed to invoke voicevox_get_version", e);
    }

    return version;
  }
}
