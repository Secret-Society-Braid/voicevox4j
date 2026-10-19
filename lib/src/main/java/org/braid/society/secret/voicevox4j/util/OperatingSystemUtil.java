package org.braid.society.secret.voicevox4j.util;

public class OperatingSystemUtil {

  private OperatingSystemUtil() {}

  public static String getDynamicLibraryExtension() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return ".dll";
    } else if (osName.contains("mac")) {
      return ".dylib";
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
      return ".so";
    } else {
      throw new UnsupportedOperationException("Unsupported operating system: " + osName);
    }
  }
}
