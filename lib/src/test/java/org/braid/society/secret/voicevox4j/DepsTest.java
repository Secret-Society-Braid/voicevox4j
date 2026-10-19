package org.braid.society.secret.voicevox4j;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DepsTest {

  private Voicevox voicevox;

  @BeforeEach
  void setUp() {
    Path projectRoot = Path.of("").toAbsolutePath().getParent();
    voicevox = new Voicevox(projectRoot.resolve(Path.of("native", "voicevox_core.dll")));
  }

  @Test
  void testGetCoreVersion() {
    String version = voicevox.getCoreVersion();

    assertThat(version).isNotNull();
  }
}
