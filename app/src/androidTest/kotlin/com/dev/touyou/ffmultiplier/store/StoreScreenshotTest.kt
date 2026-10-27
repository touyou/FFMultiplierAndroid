package com.dev.touyou.ffmultiplier.store

import android.graphics.Bitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.touyou.ffmultiplier.FFApplication
import com.dev.touyou.ffmultiplier.MainActivity
import com.dev.touyou.ffmultiplier.ScreenshotFixture
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Play ストアに載せる画面を撮る。動作の検証ではない。
 * .github/workflows/store-screenshots.yml がエミュレータで回し、filesDir/store-shots を取り出す。
 */
@RunWith(AndroidJUnit4::class)
class StoreScreenshotTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val app get() = instrumentation.targetContext.applicationContext as FFApplication

    @Test
    fun captureStoreScreenshots() {
        ScreenshotFixture.active = true
        seed()
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.waitForIdle()
            settle()
            capture("01-home")

            button("time attack").performClick()
            compose.waitForIdle()
            // 数問解いてスコアとコンボを積み、次の答えを 1 桁だけ打った所で撮る
            val start = System.currentTimeMillis()
            while (System.currentTimeMillis() - start < 12_000) solveOne()
            // 正解の表示が数字キーに重なるので、消えてから打つ
            compose.waitUntil(5_000) { !isShown("accepted") }
            tap(answer().first().toString())
            settle()
            capture("02-game")

            // 残りの時間も解き続け、ハイスコアの結果を出す
            while (!isShown("Result")) {
                runCatching { solveOne() }
                compose.waitForIdle()
            }
            settle()
            capture("03-result")

            button("EXIT").performClick()
            button("local score").performClick()
            settle()
            capture("04-local-score")
        }
    }

    private fun seed() = runBlocking {
        val store = app.scoreStore
        store.consumeFirstLaunch()
        store.setPlayerName("touyou")
        val day = 24L * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        listOf(1840, 2210, 1560, 2380, 1975, 1320).forEachIndexed { i, score ->
            store.add(score, now - (6 - i) * day)
        }
    }

    private fun operand(tag: String): Int {
        val node = compose.onNode(hasParent(hasTestTag(tag)), useUnmergedTree = true).fetchSemanticsNode()
        val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString("") { it.text }.orEmpty()
        return text.toInt(16)
    }

    private fun answer(): String = (operand("operandLeft") * operand("operandRight")).toString(16).uppercase()

    private fun solveOne() {
        answer().forEach { tap(it.toString()) }
        button("DONE").performClick()
        compose.waitForIdle()
    }

    private fun tap(digit: String) {
        compose.onNode(hasText(digit) and hasClickAction()).performClick()
    }

    private fun button(label: String) = compose.onNode(hasText(label) and hasClickAction())

    private fun isShown(text: String) =
        compose.onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()

    private fun settle() {
        compose.waitForIdle()
        Thread.sleep(1500)
    }

    private fun capture(name: String) {
        val bitmap = instrumentation.uiAutomation.takeScreenshot() ?: error("スクリーンショットが撮れなかった: $name")
        val dir = File(instrumentation.targetContext.filesDir, "store-shots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
