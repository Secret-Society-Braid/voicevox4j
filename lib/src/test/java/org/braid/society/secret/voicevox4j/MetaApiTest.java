package org.braid.society.secret.voicevox4j;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import org.braid.society.secret.voicevox4j.util.OperatingSystemUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaApiTest {
  private static Voicevox voicevox;
  private static final Logger log = LoggerFactory.getLogger(MetaApiTest.class);

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
    log.info("Core version: {}", version);
  }

  @Test
  void testRecommendedOnnxRuntimeVersion() {
    String version = voicevox.MetaEndpoint().getOnnxRuntimeRecommendedVersionedFilename();

    assertThat(version).isNotNull();
    log.info("Recommended ONNX Runtime version: {}", version);
  }

  @Test
  void testRecommendedOnnxRuntimeUnversioned() {
    String version = voicevox.MetaEndpoint().getOnnxRuntimeRecommendedUnversionedFilename();

    assertThat(version).isNotNull();
    log.info("Recommended ONNX Runtime unversioned filename: {}", version);
  }

  @Test
  void testOnnxRuntimeLibMinRequiredVersion() {
    int min = voicevox.MetaEndpoint().getOnnxRuntimeLibMinRequiredMinorVersion();

    assertThat(min).isGreaterThan(0);
    log.info("Minimum required ONNX Runtime minor version: {}", min);
  }

  @Test
  void testOnnxRuntimeLibMaxSupportedVersion() {
    int max = voicevox.MetaEndpoint().getOnnxRuntimeLibMaxSupportedMinorVersion();

    assertThat(max).isGreaterThan(0);
    log.info("Maximum supported ONNX Runtime minor version: {}", max);
  }
}
