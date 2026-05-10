# Mystery Walk — 作業引継ぎドキュメント

## プロジェクト概要
目的地を伏せた状態で「方角」と「距離」のみを頼りに歩く、Android向けお散歩アプリ。
ゲーミフィケーション（XP・レベル・バッジ）を最大の差別化要素とし、競合アプリ「Wherewalk」（iOS/500円買い切り）にはない継続利用の動機づけを提供する。

## 要件定義書
- `MysteryWalk_Requirements_Definition.md`（本ディレクトリ内に同梱）

## 確定した設計判断

### API・データソース
- **スポット検索**: OpenStreetMap (Overpass API) をメインで無料運用
  - Google Places API は使用しない（コスト理由）
  - スポットが見つからない場合はランダム座標をフォールバック
  - リワード画面の写真は Wikimedia Commons API から取得（取得不可ならカテゴリアイコンで代替）

### 安全機能
- **お助けヒント（障害物回避）**: v1ではスコープ外、v2で追加
- **歩きスマホ防止**: 振動フィードバック方式（方角に応じた振動パターン）
  - 正面(±15°): 短振動1回 / やや右(15°〜45°): 短振動2回 / 右(45°〜90°): 長振動1回 / 後方(90°超): 連続短振動
  - 振動間隔: 10秒ごと（設定で変更可能）

### GPS精度・バッテリー
- 通常時: 30秒間隔（省電力）
- 目的地200m以内: 5秒間隔（高精度に自動切替）
- フォアグラウンド時: 5秒間隔

### オフライン対応
- v1ではオフライン非対応
- 圏外時: ラストキャッシュで継続（ナビに必要なのは目的地座標+GPS+コンパスのみ）
- 新規散歩開始時のみネットワーク必須

### マネタイズ
- v1は完全無料、将来的に買い切りへ移行

## アーキテクチャ
- **言語**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **アーキテクチャ**: MVVM + Clean Architecture
- **DI**: Hilt
- **DB**: Room
- **地図**: osmdroid（OSMベース、無料）
- **ネットワーク**: Retrofit + Moshi
- **アニメーション**: Lottie Compose
- **設定保存**: DataStore Preferences
- **テスト**: JUnit5 + MockK + Turbine

### レイヤー構成
```
app/src/main/java/com/mysterywalk/app/
├── ui/           # Compose画面 (NavScreen, RewardScreen, HistoryScreen, ProfileScreen)
├── viewmodel/    # ViewModel (NavigationVM, GamificationVM, HistoryVM)
├── domain/       # UseCase (NavigateUC, FindSpotUC, CalculateXpUC)
├── data/         # Repository (LocationRepo, SpotRepo, UserRepo)
│   ├── local/    # Room DB (Entity, DAO)
│   └── remote/   # Retrofit (Overpass API, Wikimedia API)
├── service/      # フォアグラウンドサービス (LocationService)
├── di/           # Hilt Module
└── util/         # 計算ユーティリティ (距離, 方角)
```

## 実装フロー（5フェーズ）

### フェーズ0: 環境構築（1日）← 現在ここ ✅ プロジェクト作成済み
- [x] Android Studio インストール
- [x] 新規プロジェクト作成（Empty Activity, API 26, Kotlin DSL）
- [ ] Gradle依存関係の設定
- [ ] MVVMレイヤー構成のフォルダ作成
- [ ] .gitignore / Git初期化

### フェーズ1: コアナビゲーション（MVP）（1〜2週間）
- 位置情報取得 (FusedLocationProviderClient)
- パーミッションハンドリング
- Overpass API によるスポット検索
- コンパスUI + 直線距離表示
- 到着検知 (50m)
- 振動フィードバックナビ

### フェーズ2: リワード＆ゲーミフィケーション（1〜2週間）
- 到着リワード画面（アニメーション演出）
- XP・レベルシステム
- 称号バッジ

### フェーズ3: 安全＆補助機能（1週間）
- 帰還モード
- ネットワーク状態管理

### フェーズ4: 履歴＆振り返り（1週間）
- Room DB スキーマ (WalkSession, DiscoveredSpot, RoutePoint)
- osmdroid による過去ルート表示

### フェーズ5: UI/UXポリッシュ＆テスト（1〜2週間）
- 屋外視認性・ダークモード
- ユニットテスト・実機テスト

## v1 スコープ
含める: ブラインドナビ, スポット検索(OSM), 振動ナビ, リワード, XP/レベル/バッジ, 帰還モード, 履歴, 圏外継続
v2以降: お助けヒント, オフラインマップ, 同時探索, Wear OS, 買い切り課金, 到着画面からの新規目的地再設定（リピート機能）

## 開発スタイル
- ユーザーはC#/WPF(MVVM)経験者、Android開発は初めて
- バイブコーディング（AIがコード生成、ユーザーが方向性決定・実行確認）
- コードは全てAIが書き、ユーザーはAndroid Studioで実行ボタンを押す

## 次のアクション
フェーズ0の残り作業:
1. Gradle依存関係 (Hilt, Room, Retrofit, osmdroid, Lottie, DataStore等) の追加
2. パッケージ構成 (ui/viewmodel/domain/data/service/di/util) のフォルダ作成
3. Git初期化
4. フェーズ1に着手（位置情報取得から開始）
