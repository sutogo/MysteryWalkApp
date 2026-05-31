# Mystery Walk 🌌

目的地を知らされずに、コンパスが指す「方向」と「距離」だけを頼りに未知のスポットを探索する、新しい形のランダムお散歩ナビゲーションアプリです。

## ✨ 特徴 (Features)

- **ブラインド・ナビゲーション**: 目的地がどこかは到着するまでのお楽しみ。近未来的なレーダーUIに従って街を探索します。
- **サイバーパンク・エステティクス**: 深夜の都市探索をテーマにした、グラスモーフィズムとネオンカラー（Neon Explorer）の没入感あるダークテーマ。
- **ゲーミフィケーション**: 歩いた距離に応じてXPを獲得し、レベルアップ。日々の探索がゲーム感覚で楽しめます。
- **履歴＆振り返り**: 到着したスポットの履歴をマップとリストで記録。いつでも過去の冒険を振り返ることができます。
- **オフライン＆帰還モード**: ネットワークが途切れてもナビゲーションを継続。迷った時は「帰還モード」で出発地点へ確実に戻れます。

## 📱 動作環境

- Android 8.0 (API Level 26) 以上

## 🛠 技術スタック (Tech Stack)

- **言語**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **アーキテクチャ**: MVVM / Clean Architecture ベース
- **DI**: Dagger Hilt
- **データベース**: Room Database
- **ネットワーク**: Retrofit, Kotlinx Serialization
- **地図・位置情報**: FusedLocationProviderClient, SensorManager (Compass), osmdroid
- **外部 API**: Overpass API

## 🚀 インストール・ビルド方法

1. リポジトリをクローンします。
   ```bash
   git clone https://github.com/sutogo/MysteryWalkApp.git
   ```
2. Android Studio でプロジェクトを開きます。
3. 実機（USBデバッグ）またはエミュレータを接続し、実行（▶）ボタンを押してビルド＆インストールします。

## 📜 ライセンス

MIT License
