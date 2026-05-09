# フェーズ 0: 環境構築 — 実装計画

Android プロジェクトの基盤を整備し、フェーズ1（コアナビゲーション）にすぐ着手できる状態にする。

---

## User Review Required

> [!IMPORTANT]
> **パッケージ名の統一について**
> 現在のパッケージ名は `com.example.mysterywalkapp`（Android Studio テンプレートのデフォルト）です。
> HANDOFF.md では `com.mysterywalk.app` が記載されています。
> 
> **提案**: `com.mysterywalk.app` に統一します。ただし Android Studio でのリファクタリングが必要なため、以下の手順で対応します:
> - `namespace` / `applicationId` を `com.mysterywalk.app` に変更
> - ソースコードのパッケージを `com.mysterywalk.app` に変更
> - テーマ/リソースの参照も更新

> [!IMPORTANT]
> **Room バージョンについて**
> Room 3.0 (KMP対応) が alpha で出ていますが、安定性を優先し **Room 2.7.1**（最新安定版 2.x）を使用します。
> v1完成後に必要に応じて 3.0 にマイグレーションを検討します。

---

## Proposed Changes

### 1. Git 初期化

#### [MODIFY] [.gitignore](file:///c:/Users/sutogone/Documents/MysteryWalkApp/.gitignore)
- 既存の `.gitignore` はおおむね良好だが、追加のエントリ（`/app/build/`, `*.hprof` 等）を補強

#### Git コマンド実行
- `git init`
- `git add .`
- `git commit -m "Initial commit: Empty Activity project"`

---

### 2. Gradle 依存関係の設定

#### [MODIFY] [libs.versions.toml](file:///c:/Users/sutogone/Documents/MysteryWalkApp/gradle/libs.versions.toml)

以下のバージョン・ライブラリ・プラグインを追加:

| カテゴリ | ライブラリ | バージョン |
|----------|-----------|-----------|
| **DI** | Hilt (Dagger) | 2.56.2 |
| **KSP** | KSP Plugin | 2.2.10 (Kotlin バージョンと合わせる) |
| **DB** | Room | 2.7.1 |
| **ネットワーク** | Retrofit | 3.0.0 |
| **JSON** | Moshi | 1.15.2 |
| **地図** | osmdroid | 6.1.20 |
| **アニメーション** | Lottie Compose | 6.7.1 |
| **設定保存** | DataStore Preferences | 1.2.1 |
| **ナビゲーション** | Navigation Compose | 2.9.8 |
| **位置情報** | Play Services Location | 21.3.0 |
| **DI+Compose** | Hilt Navigation Compose | 1.2.0 |
| **ViewModel** | Lifecycle ViewModel Compose | 2.10.0 |

#### [MODIFY] [build.gradle.kts (root)](file:///c:/Users/sutogone/Documents/MysteryWalkApp/build.gradle.kts)
- Hilt プラグイン、KSP プラグイン を `apply false` で追加

#### [MODIFY] [build.gradle.kts (app)](file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/build.gradle.kts)
- Hilt / KSP プラグイン適用
- namespace / applicationId を `com.mysterywalk.app` に変更
- 全依存関係を追加

---

### 3. パッケージ構成フォルダの作成

#### [NEW] パッケージディレクトリ + placeholder ファイル

```
app/src/main/java/com/mysterywalk/app/
├── ui/           # Compose 画面
├── viewmodel/    # ViewModel
├── domain/       # UseCase
├── data/
│   ├── local/    # Room DB
│   └── remote/   # Retrofit API
├── service/      # フォアグラウンドサービス
├── di/           # Hilt Module
└── util/         # ユーティリティ
```

各ディレクトリに `.gitkeep` を配置してGit管理下に置く。

#### [MODIFY] [MainActivity.kt](file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/src/main/java/com/example/mysterywalkapp/MainActivity.kt)
- パッケージ名を `com.mysterywalk.app` に変更
- Hilt の `@AndroidEntryPoint` を追加

#### [NEW] MysteryWalkApplication.kt
- `@HiltAndroidApp` を付与した Application クラスを作成

#### [MODIFY] [AndroidManifest.xml](file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/src/main/AndroidManifest.xml)
- `android:name=".MysteryWalkApplication"` を application タグに追加

#### テーマファイルの移動
- `ui/theme/Color.kt`, `Theme.kt`, `Type.kt` を新パッケージに移動

---

### 4. 旧パッケージの削除

- `com/example/mysterywalkapp/` ディレクトリを削除（新パッケージに移行後）

---

## Verification Plan

### ビルド確認
- `gradlew assembleDebug` を実行してコンパイルエラーがないことを確認
- Hilt のアノテーションプロセッサが正常に動作することを確認

### 構成確認
- フォルダ構成が HANDOFF.md のレイヤー構成と一致することを確認
- Git が正しく初期化されていることを確認（`git log` で初回コミットの存在）

> [!NOTE]
> このフェーズでは実機テストは不要です（UI変更なし、基盤整備のみ）。
