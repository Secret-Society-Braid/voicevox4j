package org.braid.society.secret.voicevox4j;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import org.braid.society.secret.voicevox4j.api.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Voicevox {

  private final Linker linker;
  private final SymbolLookup lookup;
  private static final Logger log = LoggerFactory.getLogger(Voicevox.class);

  public Voicevox(Path path) {
    System.load(path.toAbsolutePath().toString());

    this.linker = Linker.nativeLinker();
    this.lookup = SymbolLookup.loaderLookup();
  }

  public Meta MetaEndpoint() {
    return new Meta(this.linker, this.lookup);
  }
}
