# voicevox4j

日本語READMEは[こちら](./docs/README.ja.md)

voicevox4j is a Java FFI wrapper for VOICEVOX CORE.

> [!NOTE]
> this project is currently under development, and implementations are subject to change.

## About

voicevox4j is a Java wrapper for the C API of [VOICEVOX CORE](https://github.com/VOICEVOX/voicevox_core).

It allows Java applications to utilize the features of VOICEVOX CORE, such as text-to-speech synthesis, Voice Model loading, and User dictionary management.

It is designed to be *java-ish*, providing an automatic resource management that used `try-with-resources` statement,
and a concise API that is easy to understand their purpose.

## Implemented Features

- [x] User dictionary management
- [x] Voice model loading
- [ ] ONNX Runtime loading support
  - see [this issue ticket](https://github.com/Secret-Society-Braid/voicevox4j/issues/1) for more information about this feature.
- ⚠️ Text-to-speech synthesis
  - implementations are blocked by ONNX Runtime loading issue.

[//]: # (## Contributing)

[//]: # ()
[//]: # (It is highly appreciated if you can contribute to this project.)

[//]: # (Please refer to the [CONTRIBUTING.md]&#40;CONTRIBUTING.md&#41; for more information about how to contribute.)

## For those who want to use this library immediately

If you are passionate to test this library, you can do so by following these steps:

> [!NOTE]
> All of relative paths are relative to the root directory of this repository.

At least Java 17 or higher is required to run this library at this time. (subjected to change)

1. Clone this repository.
2. Put the VOICEVOX CORE downloader executable in `lib/src/main/resources` directory and execute it to download a C API and necessary files.
3. Move (or copy) `lib/src/main/resources/voicevox_core/c_api/lib/voicevox_core.(so|dll|dylib)` to `lib/src/main/resources/voicevox_core`
4. Run `./gradlew test` to run the tests.

The code owner is not responsible or liable for any issues, damages, or problems that may arise from using this library immediately.

## License

This project is licensed under the [MIT License](./LICENSE) because of the license of VOICEVOX CORE.

## Acknowledgements

This project wraps the C API of [VOICEVOX CORE](https://github.com/VOICEVOX/voicevox_core)
and should be used in accordance with the VOICEVOX CORE and various Voice Model licenses.

This project may be discontinued or maintenance may be terminated without prior notice
in cases such as when contacted by the original VOICEVOX CORE developer.
