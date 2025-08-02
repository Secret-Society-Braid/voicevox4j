package org.braid.society.secret.voicevox4j.internal;

import com.sun.jna.Native;
import com.sun.jna.Library;
import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NativeVoicevoxLibrary {

  private NativeVoicevoxLibrary() {
    // Prevent instantiation
  }

  private static final String LIBRARY_NAME = "voicevox_core";

  private static String libraryExtension() {
    String osName = System.getProperty("os.name").toLowerCase();

    if (osName.contains("win")) {
      return "dll";
    } else if (osName.contains("mac")) {
      return "dylib";
    } else if (osName.contains("nux")) {
      return "so";
    } else {
      throw new UnsupportedOperationException("Unsupported OS: " + osName);
    }
  }

  public static Core load(Path libraryDirectoryPath) {
    String libraryPath;
    try {
      log.trace("Searching for voicevox_core library in directory: {}", libraryDirectoryPath);
      libraryPath = loadLibraryFromDirectory(libraryDirectoryPath);
      log.info("Loading voicevox_core library from: {}", libraryPath);
    } catch (Exception noLibraryFound) {
      try {
        log.trace("No library found in directory, falling back to resource loading.", noLibraryFound);
        libraryPath = loadLibraryFromResource("/voicevox_core/" + LIBRARY_NAME + "." + libraryExtension());
        log.info("Loading voicevox_core library from resource: {}", libraryPath);
      } catch (Exception e) {
        log.error("Failed to load voicevox_core library from both directory and resource.", e);
        log.error("Ensure you put the library file in \"**/resources/voicevox_core/{library file}\" or in the specified directory.");
        throw new RuntimeException("Failed to load voicevox_core library. Ensure the library is available in the specified directory or as a resource.", e);
      }
    }

    // UTF-8エンコーディングを強制するJNAオプションを設定
    java.util.Map<String, ?> options = Map.of(
      Library.OPTION_STRING_ENCODING, "UTF-8"
    );

    return Native.load(libraryPath, Core.class, options);
  }

  private static String loadLibraryFromDirectory(@Nonnull Path directory) {
    if(!Files.isDirectory(directory)) {
      log.error("Provided path is not a directory: {}", directory);
      throw new IllegalArgumentException("Provided path is not a directory: " + directory);
    }
    // traverse only the first level of the directory
    try(Stream<Path> files = Files.list(directory)) {
      return files
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().contains(LIBRARY_NAME))
          .filter(path -> path.getFileName().toString().endsWith(libraryExtension()))
          .findFirst()
          .map(Path::toString)
          .orElseThrow(() -> new RuntimeException("No voicevox_core library found in directory. Ensure you put the correct type of library file.: " + directory));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load library from directory: " + directory, e);
    }
  }

  private static String loadLibraryFromResource(String resourcePath) {
    try (InputStream inputStream = NativeVoicevoxLibrary.class.getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new RuntimeException("Resource not found: " + resourcePath);
      }
      
      // Create temporary file
      String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
      Path tempFile = Files.createTempFile("",fileName);
      
      // Copy resource to temporary file
      Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
      
      // Set file as executable (for Unix-like systems)
      boolean success = tempFile.toFile().setExecutable(true);
      if (!success) {
        System.err.println("Warning: Failed to set executable permission for " + tempFile);
      }
      
      // Delete on exit
      tempFile.toFile().deleteOnExit();
      
      return tempFile.toString();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load library from resource: " + resourcePath, e);
    }
  }

}
