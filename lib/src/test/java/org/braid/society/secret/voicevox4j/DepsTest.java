package org.braid.society.secret.voicevox4j;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.truth.Truth;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DepsTest {

  private Voicevox voicevox;

  @BeforeEach
  void setUp() {
    voicevox = new Voicevox(Path.of("native", "voicevox_core.dll"));
  }

  @Test
  void testGetCoreVersion() {
    String version = voicevox.getCoreVersion();

    assertThat(version).isNotNull();
  }
}
