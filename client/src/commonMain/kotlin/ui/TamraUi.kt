package ui

import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA

inline fun Container.tamraRect(
    width: Double,
    height: Double,
    color: RGBA = Colors.WHITE,
    px: Int = 0,    // x 위치.
    py: Int = 0,    // y 위치.
    hc: View? = null,  // 컨테이너 기준 수평 중앙정렬.
    vc: View? = null,  // 컨테이너 기준 수직 중앙정렬.
    callback: @ViewDslMarker SolidRect.() -> Unit = {}
) = SolidRect(width, height, color).apply {
    positionX(px)
    positionY(py)
    if (hc != null) {
        alignX(hc, 0.5, true)
    }
    if (vc != null) {
        alignY(vc, 0.5, true)
    }
}.addTo(this, callback)

inline fun Container.tamraImage(
    texture: Bitmap,
    anchorX: Double = 0.0,
    anchorY: Double = 0.0,
    px: Int = 0,    // x 위치.
    py: Int = 0,    // y 위치.
    hc: View? = null,  // 컨테이너 기준 수평 중앙정렬.
    vc: View? = null,  // 컨테이너 기준 수직 중앙정렬.
    callback: @ViewDslMarker Image.() -> Unit = {}
): Image = Image(texture, anchorX, anchorY).apply {
    positionX(px)
    positionY(py)
    if (hc != null) {
        alignX(hc, 0.5, true)
    }
    if (vc != null) {
        alignY(vc, 0.5, true)
    }
}.addTo(this, callback)

fun Any.pad(n: Int) = toString().padStart(n, '0')