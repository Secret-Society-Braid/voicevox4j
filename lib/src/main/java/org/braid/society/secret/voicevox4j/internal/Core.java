package org.braid.society.secret.voicevox4j.internal;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.braid.secret.society.voicevox4j.internal.structs.*;
import org.braid.society.secret.voicevox4j.internal.structs.OpenJtalkRc;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxInitializeOptions;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxLoadOnnxruntimeOptions;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxOnnxruntime;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxSynthesisOptions;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxSynthesizer;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxTtsOptions;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxUserDict;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxUserDictWord;
import org.braid.society.secret.voicevox4j.internal.structs.VoicevoxVoiceModelFile;

/**
 * JNAインターフェースとして、Cライブラリに存在する関数をマッピングしたクラスです。
 * <p>
 * 構造体や列挙型のマッピングは{@link org.braid.secret.society.voicevox4j.internal.structs}パッケージにあります。
 * @apiNote
 * このインターフェースマッピングは、<a href="https://voicevox.github.io/voicevox_core/apis/c_api/voicevox__core_8h.html">公式のC APIドキュメント</a>
 * に基づいています。
 * 意図しない挙動、またはロードエラーが発生した場合は、お手数ですが公式のドキュメントと課題トラッカーを確認し、{@code voicevox4j}のみの問題かどうかの確認をお願いします。
 * @see NativeVoicevoxLibrary
 * @see <a href="https://voicevox.github.io/voicevox_core/apis/c_api/voicevox__core_8h.html">VOICEVOX CORE 公式 C APIドキュメント</a>
 * @see <a href="https://github.com/VOICEVOX/voicevox_core">VOICEVOX CORE のリポジトリ</a>
 */
public interface Core extends Library {

  // ONNX Runtime関連
  // #if defined(VOICEVOX_LOAD_ONNXRUNTIME)
  String voicevox_get_onnxruntime_lib_versioned_filename();
  String voicevox_get_onnxruntime_lib_unversioned_filename();
  VoicevoxLoadOnnxruntimeOptions.ByValue voicevox_make_default_load_onnxruntime_options();
  // #endif

  VoicevoxOnnxruntime voicevox_onnxruntime_get();

  // #if defined(VOICEVOX_LOAD_ONNXRUNTIME)
  int voicevox_onnxruntime_load_once(VoicevoxLoadOnnxruntimeOptions options, PointerByReference out_onnxruntime);
  // #endif

  // #if defined(VOICEVOX_LINK_ONNXRUNTIME)
  // int voicevox_onnxruntime_init_once(PointerByReference out_onnxruntime); // iOS only
  // #endif

  int voicevox_onnxruntime_create_supported_devices_json(VoicevoxOnnxruntime onnxruntime, PointerByReference output_supported_devices_json);

  // OpenJtalkRc関連
  int voicevox_open_jtalk_rc_new(String open_jtalk_dic_dir, PointerByReference out_open_jtalk);
  int voicevox_open_jtalk_rc_use_user_dict(OpenJtalkRc open_jtalk, VoicevoxUserDict user_dict);
  int voicevox_open_jtalk_rc_analyze(OpenJtalkRc open_jtalk, String text, PointerByReference output_accent_phrases_json);
  void voicevox_open_jtalk_rc_delete(OpenJtalkRc open_jtalk);

  // VoicevoxSynthesizer関連
  VoicevoxInitializeOptions.ByValue voicevox_make_default_initialize_options();
  int voicevox_synthesizer_new(VoicevoxOnnxruntime onnxruntime, OpenJtalkRc open_jtalk, VoicevoxInitializeOptions options, PointerByReference out_synthesizer);
  void voicevox_synthesizer_delete(VoicevoxSynthesizer synthesizer);
  int voicevox_synthesizer_load_voice_model(VoicevoxSynthesizer synthesizer, VoicevoxVoiceModelFile model);
  int voicevox_synthesizer_unload_voice_model(VoicevoxSynthesizer synthesizer, byte[] model_id);
  VoicevoxOnnxruntime voicevox_synthesizer_get_onnxruntime(VoicevoxSynthesizer synthesizer);
  boolean voicevox_synthesizer_is_gpu_mode(VoicevoxSynthesizer synthesizer);
  boolean voicevox_synthesizer_is_loaded_voice_model(VoicevoxSynthesizer synthesizer, byte[] model_id);
  Pointer voicevox_synthesizer_create_metas_json(VoicevoxSynthesizer synthesizer);

  int voicevox_synthesizer_create_audio_query_from_kana(VoicevoxSynthesizer synthesizer, String kana, int style_id, PointerByReference output_audio_query_json);
  int voicevox_synthesizer_create_audio_query(VoicevoxSynthesizer synthesizer, String text, int style_id, PointerByReference output_audio_query_json);
  int voicevox_synthesizer_create_accent_phrases_from_kana(VoicevoxSynthesizer synthesizer, String kana, int style_id, PointerByReference output_accent_phrases_json);
  int voicevox_synthesizer_create_accent_phrases(VoicevoxSynthesizer synthesizer, String text, int style_id, PointerByReference output_accent_phrases_json);
  int voicevox_synthesizer_replace_mora_data(VoicevoxSynthesizer synthesizer, String accent_phrases_json, int style_id, PointerByReference output_accent_phrases_json);
  int voicevox_synthesizer_replace_phoneme_length(VoicevoxSynthesizer synthesizer, String accent_phrases_json, int style_id, PointerByReference output_accent_phrases_json);
  int voicevox_synthesizer_replace_mora_pitch(VoicevoxSynthesizer synthesizer, String accent_phrases_json, int style_id, PointerByReference output_accent_phrases_json);

  VoicevoxSynthesisOptions.ByValue voicevox_make_default_synthesis_options();
  int voicevox_synthesizer_synthesis(VoicevoxSynthesizer synthesizer, String audio_query_json, int style_id, VoicevoxSynthesisOptions options, IntByReference output_wav_length, PointerByReference output_wav);

  VoicevoxTtsOptions.ByValue voicevox_make_default_tts_options();
  int voicevox_synthesizer_tts_from_kana(VoicevoxSynthesizer synthesizer, String kana, int style_id, VoicevoxTtsOptions options, IntByReference output_wav_length, PointerByReference output_wav);
  int voicevox_synthesizer_tts(VoicevoxSynthesizer synthesizer, String text, int style_id, VoicevoxTtsOptions options, IntByReference output_wav_length, PointerByReference output_wav);

  // VoicevoxVoiceModelFile関連
  int voicevox_voice_model_file_open(String path, PointerByReference out_model);
  void voicevox_voice_model_file_id(VoicevoxVoiceModelFile model, byte[] output_voice_model_id);
  Pointer voicevox_voice_model_file_create_metas_json(VoicevoxVoiceModelFile model);
  void voicevox_voice_model_file_delete(VoicevoxVoiceModelFile model);

  // メモリ解放関連
  void voicevox_json_free(Pointer json);
  void voicevox_wav_free(Pointer wav);

  // エラーメッセージ
  String voicevox_error_result_to_message(int result_code);

  // ユーザー辞書関連
  VoicevoxUserDictWord.ByValue voicevox_user_dict_word_make(String surface, String pronunciation, long accent_type);
  Pointer voicevox_user_dict_new();
  int voicevox_user_dict_load(VoicevoxUserDict user_dict, String dict_path);
  int voicevox_user_dict_add_word(VoicevoxUserDict user_dict, VoicevoxUserDictWord word, byte[] output_word_uuid); // `uint8_t (*)[16]` は byte[16]として扱う
  int voicevox_user_dict_update_word(VoicevoxUserDict user_dict, byte[] word_uuid, VoicevoxUserDictWord word); // `uint8_t (*)[16]` は byte[16]として扱う
  int voicevox_user_dict_remove_word(VoicevoxUserDict user_dict, byte[] word_uuid); // `uint8_t (*)[16]` は byte[16]として扱う
  int voicevox_user_dict_to_json(VoicevoxUserDict user_dict, PointerByReference output_json);
  int voicevox_user_dict_import(VoicevoxUserDict user_dict, VoicevoxUserDict other_dict);
  int voicevox_user_dict_save(VoicevoxUserDict user_dict, String path);
  void voicevox_user_dict_delete(VoicevoxUserDict user_dict);

  // その他
  String voicevox_get_version();
}
