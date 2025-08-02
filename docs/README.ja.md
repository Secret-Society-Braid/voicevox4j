# voicevox4j

voicevox4jはVOICEVOX COREのJava FFIラッパーです。

> [!NOTE]
> このプロジェクトは現在開発中であり、実装は変更される可能性があります。

## 概要

voicevox4jは、公式リポジトリより提供されているVOICEVOX COREのC APIをJavaから利用するためのラッパーライブラリです。

Javaアプリケーションから音声合成機能、音声モデルの読み込み、ユーザ辞書の読み込みなどのコア機能を利用できます。

また、この実装ではより「Javaらしい」実装を目指しており、`try-with-resources`を利用したJavaスタイルのリソース管理や、
目的がわかりやすい簡潔なAPIを目指しています。

## 実装済み機能

- [x] ユーザ辞書の管理
- [x] 音声モデルの読み込み
- [ ] ONNX Runtimeの読み込みサポート
  - この機能の詳細については、[このissueチケット](https://github.com/Secret-Society-Braid/voicevox4j/issues/1)を参照してください。
- ⚠️ 音声合成機能
  - ONNX Runtimeの読み込みが実装されていないため、音声合成機能は未テスト状態です。

[//]: # (## コントリビューション)

[//]: # ()
[//]: # (このプロジェクトへのコントリビューションは大歓迎です。)

[//]: # (コントリビューションの方法については、[CONTRIBUTING.md]&#40;./CONTRIBUTING.md&#41;を参照してください。)

## このライブラリを今すぐに使いたい方へ

このラッパーが開発途中であることを承知の上で、テストなどを行いたい方は以下の手順で実行できます。

> [!NOTE]
> すべての相対パスは、このリポジトリのルートディレクトリを基準としています。

少なくともJava 17以上が必要です。（将来変更する可能性があります）

1. このリポジトリをクローンします。
2. VOICEVOX COREのダウンローダーを`lib/src/main/resources`ディレクトリに配置し、実行してC APIと必要なファイルをダウンロードします。
3. `lib/src/main/resources/voicevox_core/c_api/lib/voicevox_core.(so|dll|dylib)`を`lib/src/main/resources/voicevox_core`に移動（またはコピー）します。
4. `./gradlew test`でテストを実行します。

現時点でのこのライブラリの使用に関して、コードオーナーは一切の問題、損害、トラブルについて責任を負いません。
開発途中、また未実装の機能があることを十分に理解した上でご利用ください。

## ライセンス

このプロジェクトは、VOICEVOX COREのライセンスに基づき、[MIT License](./LICENSE)でライセンスされています。

## 謝辞

このプロジェクトは、[VOICEVOX CORE](https://github.com/VOICEVOX/voicevox_core)のC APIをラップしており、
VOICEVOX COREや各種音声モデルのライセンスに従って使用する必要があります。

このプロジェクトは、VOICEVOX COREの開発者から連絡があった場合などに、
事前の通知なしに中止またはメンテナンスが終了する可能性があります。
