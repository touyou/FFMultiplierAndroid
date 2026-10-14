# FFMultiplier (Android)

16 進数の九九(0〜F × 0〜F)を 60 秒で解くタイムアタック。iOS 版は [touyou/FFMultiply](https://github.com/touyou/FFMultiply)。

## 構成

- Kotlin + Jetpack Compose、1 Activity。targetSdk / compileSdk 37、minSdk 24
- ローカルスコア: DataStore(旧版の Realm のスコアは引き継がない。iOS 版と同じ判断)
- オンラインランキング: Firebase Realtime Database。iOS 版と同じデータを共有し、匿名認証の uid で
  `scores_v2/<uid>` に書く。旧版が `scores/<広告 ID>` に書いたレコードは読み取りだけ続け、
  `scores_v2` に書けた端末は自分の旧レコードを消す。ルールは iOS リポジトリの `database.rules.json`
- 広告: Google Mobile Ads(バナー / インタースティシャル)

## ビルド

```sh
./gradlew testDebugUnitTest assembleDebug
./gradlew bundleRelease   # keystore.properties(雛形は keystore.properties.example)が必要
```

`app/google-services.json` は Firebase プロジェクト `ffmultiplier` の Android アプリ設定。

## バージョン

`gradle.properties` の `FF_VERSION_CODE` / `FF_VERSION_NAME` を上げる。Play に公開済みの最後は Java 版 1.3.0(versionCode 6)。
