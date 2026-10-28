package com.dev.touyou.ffmultiplier

/** ストア用の撮影中だけ立てる(androidTest の StoreScreenshotTest)。広告とランキングへの書き込みを止める。 */
object ScreenshotFixture {
    @Volatile
    var active: Boolean = false
}
