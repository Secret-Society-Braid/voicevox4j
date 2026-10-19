package org.braid.society.secret.voicevox4j.api;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides access to the metadata (like core, onnx runtime versions, etc.) of the native library.
 * <p>
 * Note that `Meta` naming is not the official naming; thus there is no relationship with the official library.
 * The name was chosen for this library.
 * <p>
 * If you want, please check each method's documentation for the original C function signature.
 */
public class Meta {
  private static final Logger log = LoggerFactory.getLogger(Meta.class);
  private final Linker linker;
  private final SymbolLookup lookup;

  public Meta(Linker linker, SymbolLookup lookup) {
    this.linker = linker;
    this.lookup = lookup;
  }

  /**
   * Get the version of the library that is currently loaded.
   * <p>
   * Original C function signature: <code>const char * voicevox_get_version (void)</code>
   * @return The version string of the library read from the native library.
   */
  public String getCoreVersion() {
    MethodHandle getCoreVersionHandle = this.linker.downcallHandle(
      this.lookup.find("voicevox_get_version").orElseThrow(() -> new RuntimeException("Failed to find symbol")),
      FunctionDescriptor.of(ValueLayout.ADDRESS)
    );

    String version;

    try {
      MemorySegment versionPtr = (MemorySegment) getCoreVersionHandle.invokeExact();
      version = versionPtr.reinterpret(Long.MAX_VALUE).getString(0);
      log.debug("voicevox_get_version returned: {}", version);
    } catch (Throwable e) {
      log.error("Error occurred while calling voicevox_get_version", e);
      throw new RuntimeException(e);
    }

    return version;
  }

  /**
   * Get the version of the ONNX Runtime filename that is recommended to be used with the library.
   * <p>
   * Original C function signature: <code>const char * voicevox_get_onnxruntime_lib_recommended_versioned_filename (void)</code>
   * @return The version string of the ONNX Runtime filename read from the native library.
   */
  public String getOnnxRuntimeRecommendedVersionedFilename() {
    MethodHandle getOnnxRuntimeRecommendedVersionedFilenameHandle = this.linker.downcallHandle(
      this.lookup.find("voicevox_get_onnxruntime_lib_recommended_versioned_filename")
        .orElseThrow(() -> new RuntimeException("Failed to find symbol")),
      FunctionDescriptor.of(ValueLayout.ADDRESS)
    );

    String filename;

    try {
      MemorySegment filenamePtr = (MemorySegment) getOnnxRuntimeRecommendedVersionedFilenameHandle.invokeExact();
      filename = filenamePtr.reinterpret(Long.MAX_VALUE).getString(0);
      log.debug("voicevox_get_onnxruntime_lib_recommended_versioned_filename returned: {}",
        filename);
    } catch (Throwable e) {
      log.error(
        "Error occurred while calling voicevox_get_onnxruntime_lib_recommended_versioned_filename",
        e);
      throw new RuntimeException(e);
    }

    return filename;
  }
}
