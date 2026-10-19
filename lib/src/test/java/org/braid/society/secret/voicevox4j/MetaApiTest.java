package org.braid.society.secret.voicevox4j;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import org.braid.society.secret.voicevox4j.util.OperatingSystemUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MetaApiTest {
  private static Voicevox voicevox;

  @BeforeAll
  static void setUp() {
    Path projectRoot = Path.of("").toAbsolutePath().getParent();
    final String libName = "voicevox_core%s".formatted(OperatingSystemUtil.getDynamicLibraryExtension());
    voicevox = new Voicevox(projectRoot.resolve(Path.of("native", libName)));
  }

  @Test
  void testGetCoreVersion() {
    String version = voicevox.MetaEndpoint().getCoreVersion();

    assertThat(version).isNotNull();
  }

  @Test
  void testRecommendedOnnxRuntimeVersion() {
    String version = voicevox.MetaEndpoint().getOnnxRuntimeRecommendedVersionedFilename();

    assertThat(version).isNotNull();
  }
}
