package org.braid.secret.society.voicevox4j;


import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.braid.secret.society.voicevox4j.api.OpenJTalkDictionary;
import org.braid.secret.society.voicevox4j.api.Synthesizer;
import org.braid.secret.society.voicevox4j.api.VoiceModelFile;
import org.braid.secret.society.voicevox4j.exception.VoicevoxException;
import org.braid.secret.society.voicevox4j.internal.Core;
import org.braid.secret.society.voicevox4j.internal.NativeVoicevoxLibrary;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxInitializeOptions;
import org.braid.secret.society.voicevox4j.internal.structs.VoicevoxOnnxruntime;

/**
 * Voicevoxコアライブラリを操作するためのトップエントリポイントクラスです。
 *
 */
@Slf4j
public class Voicevox {

  private final Core core;

  /**
   * {@code voicevox_core}ライブラリを、指定したディレクトリ直下を探してロードします。
   * <p>
   * 指定したディレクトリにライブラリが存在しなかった場合、
   * ミドルユーザーでの使用と推定し、リソースディレクトリの中にある{@code voicevox_core}ディレクトリ直下を探し、ロードを試みます。
   * （つまり、{@code resources/voicevox_core/}にライブラリが存在することを期待します。）
   * @param voicevoxCoreLibPath voicevox_coreライブラリが存在するディレクトリのパス。
   */
  public Voicevox(Path voicevoxCoreLibPath) {
    this.core = NativeVoicevoxLibrary.load(voicevoxCoreLibPath);
    log.debug("Voicevox core library loaded from: {}", voicevoxCoreLibPath);
  }

  /**
   * 指定された音声モデルファイルを開き、VOICEVOXコアライブラリを使用して使用可能な状態にします。
   * @param vvmPath 音声モデルファイル {@code *.vvm} のパス。
   * @return 音声モデルを格納する {@link VoiceModelFile} オブジェクト
   * @throws VoicevoxException 読み書きエラーなど、ネイティブライブラリの関数呼び出しが成功以外を返した場合。
   */
  public VoiceModelFile useVoiceModelFile(Path vvmPath) throws VoicevoxException {
    log.debug("Opening voice model file: {}", vvmPath);
    return new VoiceModelFile(vvmPath, core);
  }

  /**
   * OpenJTalk辞書機能を初期化します。
   * この段階では辞書ファイルの読み込みは<b>行われません。</b>
   * @param openJtalkDicDir OpenJTalk辞書バイナリが存在するディレクトリのパス。
   * @return 初期化されたOpenJTalk辞書オブジェクト
   * @throws VoicevoxException 読み書きエラーなど、ネイティブライブラリの関数呼び出しが成功以外を返した場合。
   */
  public OpenJTalkDictionary initOpenJTalkDictionary(Path openJtalkDicDir) throws VoicevoxException {
    log.debug("Initializing OpenJTalk dictionary from: {}", openJtalkDicDir);
    return new OpenJTalkDictionary(openJtalkDicDir, core);
  }

  /**
   * 新しい音声合成シンセサイザーを作成します。
   * この操作ではONNXランタイムが初期化され、指定されたOpenJTalk辞書と組み合わせて合成器が作成されます。
   *
   * @param openJtalkDictionary 使用するOpenJTalk辞書
   * @return 初期化された音声合成器
   * @throws VoicevoxException 合成器の作成に失敗した場合
   */
  public Synthesizer createSynthesizer(OpenJTalkDictionary openJtalkDictionary) throws VoicevoxException {
    log.debug("Creating synthesizer with OpenJTalk dictionary");

    // ONNXランタイムを取得
    VoicevoxOnnxruntime onnxruntime = core.voicevox_onnxruntime_get();

    // Synthesizerを作成
    return new Synthesizer(onnxruntime, openJtalkDictionary, core);
  }

  /**
   * 新しい音声合成シンセサイザーを作成します（初期化オプション指定）。
   * この操作ではONNXランタイムが初期化され、指定されたOpenJTalk辞書と組み合わせて合成器が作成されます。
   *
   * @param openJtalkDictionary 使用するOpenJTalk辞書
   * @param options 初期化オプション
   * @return 初期化された音声合成器
   * @throws VoicevoxException 合成器の作成に失敗した場合
   */
  public Synthesizer createSynthesizer(OpenJTalkDictionary openJtalkDictionary, VoicevoxInitializeOptions options) throws VoicevoxException {
    log.debug("Creating synthesizer with OpenJTalk dictionary and custom options");

    // ONNXランタイムを取得
    VoicevoxOnnxruntime onnxruntime = core.voicevox_onnxruntime_get();

    // Synthesizerを作成
    return new Synthesizer(onnxruntime, openJtalkDictionary, options, core);
  }
}
