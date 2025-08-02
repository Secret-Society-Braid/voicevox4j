package org.braid.society.secret.voicevox4j;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;

public class DepsTest {

  private Voicevox voicevox;

  @BeforeEach
  void setUp() {
    voicevox = new Voicevox(Path.of(""));
  }
}
