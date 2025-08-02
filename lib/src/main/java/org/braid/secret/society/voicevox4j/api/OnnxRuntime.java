package org.braid.secret.society.voicevox4j.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.braid.secret.society.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxLoadOnnxruntimeOptions;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxOnnxruntime;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxResultCode;

@Slf4j
public class OnnxRuntime {

  private final Core core;
  private final VoicevoxOnnxruntime nativeOnnxruntime;

  public OnnxRuntime(Core core, Path ortPath) throws VoicevoxException {
    this.core = core;
    String originalEncoding = System.getProperty("jna.encoding");
    System.setProperty("jna.encoding", "UTF-8");
    try {
      VoicevoxLoadOnnxruntimeOptions options;
      if (ortPath == null) {
        // use default options to load ONNX Runtime
        log.debug("Using default ONNX Runtime options.");
        options = core.voicevox_make_default_load_onnxruntime_options();
      } else {
        // use specified ONNX Runtime path with enhanced UTF-8 handling
        String pathString = ortPath.toAbsolutePath().normalize().toString();
        log.debug("Using specified ONNX Runtime path: {}", pathString);

        options = new VoicevoxLoadOnnxruntimeOptions();

        options.filename = pathString;
        log.debug("Successfully set UTF-8 path: {}", pathString);
      }

      PointerByReference onnxruntimeRef = new PointerByReference();

      int result = core.voicevox_onnxruntime_load_once(options, onnxruntimeRef);
      if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
        String errorMessage = core.voicevox_error_result_to_message(result);
        throw new VoicevoxException("Failed to load ONNX Runtime: " + errorMessage, result);
      }

      this.nativeOnnxruntime = new VoicevoxOnnxruntime(Pointer.nativeValue(onnxruntimeRef.getValue()));
    } finally {
      if(originalEncoding != null) {
        System.setProperty("jna.encoding", originalEncoding);
      } else {
        System.clearProperty("jna.encoding");
      }
    }
  }

  public OnnxRuntime(Core core) throws VoicevoxException {
    this(core, null);
  }

  public VoicevoxOnnxruntime getNativeOnnxruntime() {
    return nativeOnnxruntime;
  }
}
