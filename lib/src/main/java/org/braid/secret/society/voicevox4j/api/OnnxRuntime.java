package org.braid.secret.society.voicevox4j.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  public OnnxRuntime(Core core) throws VoicevoxException {
    this.core = core;
    VoicevoxLoadOnnxruntimeOptions options = core.voicevox_make_default_load_onnxruntime_options();

    PointerByReference onnxruntimeRef = new PointerByReference();

    int result = core.voicevox_onnxruntime_load_once(options, onnxruntimeRef);
    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to load ONNX Runtime with default options: " + errorMessage, result);
    }

    this.nativeOnnxruntime = new VoicevoxOnnxruntime(Pointer.nativeValue(onnxruntimeRef.getValue()));
  }

  public OnnxRuntime(Core core, VoicevoxLoadOnnxruntimeOptions options) throws VoicevoxException {
    this.core = core;

    PointerByReference onnxruntimeRef = new PointerByReference();

    int result = core.voicevox_onnxruntime_load_once(options, onnxruntimeRef);
    if (result != VoicevoxResultCode.VOICEVOX_RESULT_OK) {
      String errorMessage = core.voicevox_error_result_to_message(result);
      throw new VoicevoxException("Failed to load ONNX Runtime with custom options: " + errorMessage, result);
    }

    this.nativeOnnxruntime = new VoicevoxOnnxruntime(Pointer.nativeValue(onnxruntimeRef.getValue()));
  }

  public VoicevoxOnnxruntime getNativeOnnxruntime() {
    return nativeOnnxruntime;
  }
}
