# フェーズ 5: UI/UX ポリッシュ（デザイン刷新）

ユーザー様からの「現状のUIデザインがチープで魅力を感じない」というフィードバックに基づき、フェーズ5では **没入感と特別感のあるダークテーマUI** への全面リニューアルを行います。

## 🎨 デザインコンセプト案: "Neon Explorer" (ネオン・エクスプローラー)

「未知の場所を探索する」というMystery Walkのコンセプトを強調するため、夜間の散歩やサイバーパンク/近未来感を連想させる **深みのあるダーク背景 × 発光するネオンアクセント** を提案します。

````carousel
![ナビゲーション画面のイメージ案（シアン）](C:\Users\sutogone\.gemini\antigravity\brain\0d2374eb-0c61-458f-b28c-d24ca45e2069\mystery_walk_nav_ui_1778385111973.png)
<!-- slide -->
![ナビゲーション画面のイメージ案（パープル）](C:\Users\sutogone\.gemini\antigravity\brain\0d2374eb-0c61-458f-b28c-d24ca45e2069\mystery_walk_nav_ui_purple_1778385302627.png)
<!-- slide -->
![リワード（到着）画面のイメージ案](C:\Users\sutogone\.gemini\antigravity\brain\0d2374eb-0c61-458f-b28c-d24ca45e2069\mystery_walk_reward_ui_1778385128001.png)
````

### デザインの3つの柱
1. **プレミアム・ダークネス**: 背景を単なる黒ではなく、深いスレート（`#0F172A`）やミッドナイトブルーにし、奥行きを持たせます。
2. **グラスモーフィズム**: カードやボタンを半透明にし、背景がうっすらと透けるようなモダンな質感を適用します。
3. **ネオン・タイポグラフィ**: 距離や獲得XPの表示に洗練されたフォント（Google Fontsの「Outfit」などを想定）を使用し、グラデーションや発光エフェクト（ドロップシャドウの流用）をかけます。

---

## User Review Required

> [!IMPORTANT]
> シアン（水色）とパープル（紫色）の2パターンのコンパス画面をご用意しました！
> どちらのアクセントカラーを基調としてコーディングを進めるか、お好みの色を教えてください。

## Proposed Changes (実装予定の変更点)

### 1. テーマとカラーパレットの刷新
#### [MODIFY] `Theme.kt` & `Color.kt` (file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/src/main/java/com/mysterywalk/app/ui/theme/Theme.kt)
- `MaterialTheme` を **ダークモード固定** または **ダークモードをデフォルト** とするよう設定。
- プライマリーカラーを「ネオンシアン（`#00F0FF`）」や「エレクトリックパープル（`#A855F7`）」に変更。

### 2. コンポーネントのデザイン変更
#### [MODIFY] `NavScreen.kt` (file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/src/main/java/com/mysterywalk/app/ui/navigation/NavScreen.kt)
- **コンパス（CompassArrow）の大幅な進化**: 単なる三角形から、光るリングとスタイリッシュな矢印を組み合わせたレーダーのようなデザインに変更。
- **距離テキスト**: 巨大で美しいフォントウェイトを使用し、ネオンの輝きを付与。
- **ボタン**: アウトラインや半透明（グラスモーフィズム）を用いた未来的なボタンへ。

#### [MODIFY] `RewardScreen.kt` (file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/src/main/java/com/mysterywalk/app/ui/reward/RewardScreen.kt)
- **写真フレーム**: 到着地の写真を、半透明のガラス風カードに配置。
- **XP・レベル表示**: ゴールドグラデーションを用いた特別感のあるテキストへ変更。
- **パーティクル**: 既存のLottieアニメーションに加え、背景でゆっくり動く星屑のようなエフェクト（Canvas描画）を追加。

#### [MODIFY] `HistoryScreen.kt` (file:///c:/Users/sutogone/Documents/MysteryWalkApp/app/src/main/java/com/mysterywalk/app/ui/history/HistoryScreen.kt)
- リストのカードデザインをダークテーマ対応の洗練されたデザインに引き上げ。
- マップ（osmdroid）を可能であればダークスタイルに設定（OSMのダークタイル、もしくはカラーフィルターを適用）。

### 3. フォントの導入
#### [NEW] `google_fonts` 依存関係の追加、または `res/font` へのダウンロードフォント追加
- UI全体を洗練させるため、デフォルトのRobotoではなく、近未来感のあるジオメトリック・サンセリフ（例：`Outfit` や `Inter`）を導入。

## Verification Plan
1. `assembleDebug` でビルドが通るか確認する。
2. アプリを起動し、トップ画面、コンパス画面、履歴画面、リワード画面を通しで確認し、意図したダークテーマやエフェクトが美しく適用されているかを（ユーザー様に実機で）検証していただく。
