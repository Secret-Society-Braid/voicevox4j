package org.braid.secret.society.voicevox4j;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DepsTest {

  private Voicevox voicevox;

  @BeforeEach
  void setUp() {
    voicevox = new Voicevox(Path.of(""));
  }
}
