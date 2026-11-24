package com.dev.touyou.ffmultiplier.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * 表示部と操作部を 2 つに分けて置く。半開きの折り目があれば折り目に沿って分け(iOS の
 * ArrangementView と同じ考え)、無ければ横長は左右・縦長は上下に半分ずつ。
 * 中身にはどちらで分けたかを渡す(左右のときに操作を寄せ直すため)。
 */
@Composable
fun HingeSplit(
    first: @Composable (sideBySide: Boolean) -> Unit,
    second: @Composable (sideBySide: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hinge = currentWindowAdaptiveInfo().windowPosture.hingeList.firstOrNull { it.isSeparating }
    var origin by remember { mutableStateOf<Rect?>(null) }
    val density = LocalDensity.current
    BoxWithConstraints(modifier.fillMaxSize().onGloballyPositioned { origin = it.boundsInWindow() }) {
        val sideBySide = hinge?.isVertical ?: (maxWidth > maxHeight)
        val total = if (sideBySide) maxWidth else maxHeight
        // 折り目は window 座標で来るので、この領域の位置を引いてから使う。端に寄りすぎていれば使わない
        val split = if (hinge != null && origin != null) {
            val o = origin!!
            with(density) {
                val start = (if (sideBySide) hinge.bounds.left - o.left else hinge.bounds.top - o.top).toDp()
                val gap = (if (sideBySide) hinge.bounds.width else hinge.bounds.height).toDp()
                (start to gap).takeIf { start >= 160.dp && start + gap <= total - 160.dp }
            }
        } else {
            null
        }
        val (firstSize, gap) = split ?: ((total / 2) to 0.dp)
        if (sideBySide) {
            // 折り目の位置は左からの距離なので、RTL でも並びは物理的な左から。中身の向きは元に戻す
            val direction = LocalLayoutDirection.current
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row {
                    Box(Modifier.width(firstSize).fillMaxHeight()) {
                        CompositionLocalProvider(LocalLayoutDirection provides direction) { first(sideBySide) }
                    }
                    Spacer(Modifier.width(gap))
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        CompositionLocalProvider(LocalLayoutDirection provides direction) { second(sideBySide) }
                    }
                }
            }
        } else {
            Column {
                Box(Modifier.height(firstSize).fillMaxWidth()) { first(sideBySide) }
                Spacer(Modifier.height(gap))
                Box(Modifier.weight(1f).fillMaxWidth()) { second(sideBySide) }
            }
        }
    }
}
