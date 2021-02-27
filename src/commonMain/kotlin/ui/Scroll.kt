package ui

import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import tamra.windowWidth

inline fun Container.uiHorizontalScrollableArea(
    width: Double = windowWidth.toDouble(),
    height: Double = 100.0,
    contentWidth: Double = 512.0,
    contentHeight: Double = 100.0,
    buttonSize: Double = 16.0,
    config: UIHorizontalScrollableArea.() -> Unit = {},
    block: @ViewDslMarker Container.() -> Unit = {}
): UIHorizontalScrollableArea = UIHorizontalScrollableArea(width, height, contentWidth, contentHeight, buttonSize)
    .addTo(this).apply(config).also { block(it.container) }

// @TODO: Optimize this!
// @TODO: Add an actualContainer = this inside Container
open class UIHorizontalScrollableArea(
    width: Double = windowWidth.toDouble(),
    height: Double = 50.0,
    contentWidth: Double = 512.0,
    contentHeight: Double = 50.0,
    buttonSize: Double = 16.0,
) : UIView(width, height) {

    var buttonSize by uiObservable(buttonSize) { onSizeChanged() }

    var contentWidth by uiObservable(contentWidth) { onSizeChanged() }
    var contentHeight by uiObservable(contentHeight) { onSizeChanged() }

    var stepRatio by uiObservable(0.1) { onSizeChanged() }

    val clipContainer = clipContainer(width, height)
    val container = clipContainer.fixedSizeContainer(contentWidth, contentHeight)

    val horScrollBar = uiScrollBar(width = width, height = buttonSize, buttonSize = buttonSize, skin = DefaultHorScrollBarSkin) {
        onChange { this@UIHorizontalScrollableArea.onMoved() }
        addTo(clipContainer)
    }

    init {
        calculateSizes()
    }

    override fun onSizeChanged() {
        super.onSizeChanged()
        calculateSizes()
    }

    private fun calculateSizes() {
        horScrollBar.totalSize = contentWidth
        horScrollBar.pageSize = width
        horScrollBar.stepSize = width * stepRatio

        clipContainer.size(width, height)
        container.size(contentWidth, contentHeight)

        horScrollBar.size(width, buttonSize)
        horScrollBar.position(0.0, height - buttonSize)
    }

    protected open fun onMoved() {
        container.x = -horScrollBar.current
    }
}


inline fun Container.uiVerticalScrollableArea(
    width: Double = windowWidth.toDouble(),
    height: Double = 128.0,
    contentWidth: Double = windowWidth.toDouble(),
    contentHeight: Double = 512.0,
    buttonSize: Double = 16.0,
    config: UIVerticalScrollableArea.() -> Unit = {},
    block: @ViewDslMarker Container.() -> Unit = {},
): UIVerticalScrollableArea = UIVerticalScrollableArea(width, height, contentWidth, contentHeight, buttonSize)
    .addTo(this).apply(config).also { block(it.container) }

// @TODO: Optimize this!
// @TODO: Add an actualContainer = this inside Container
open class UIVerticalScrollableArea(
    width: Double = windowWidth.toDouble(),
    height: Double = 128.0,
    contentWidth: Double = 128.0,
    contentHeight: Double = 512.0,
    buttonSize: Double = 16.0,
) : UIView(width, height) {

    var buttonSize by uiObservable(buttonSize) { onSizeChanged() }

    var contentWidth by uiObservable(contentWidth) { onSizeChanged() }
    var contentHeight by uiObservable(contentHeight) { onSizeChanged() }

    var stepRatio by uiObservable(0.1) { onSizeChanged() }

    val clipContainer = clipContainer(width, height)
    val container = clipContainer.fixedSizeContainer(contentWidth, contentHeight)

    val verScrollBar = uiScrollBar(width = buttonSize, height = height, buttonSize = buttonSize, skin = DefaultVerScrollBarSkin) {
        onChange { this@UIVerticalScrollableArea.onMoved() }
        addTo(clipContainer)
    }

    init {
        calculateSizes()
    }

    override fun onSizeChanged() {
        super.onSizeChanged()
        calculateSizes()
    }

    private fun calculateSizes() {
        verScrollBar.totalSize = contentHeight
        verScrollBar.pageSize = height
        verScrollBar.stepSize = height * stepRatio

        clipContainer.size(width, height)
        container.size(contentWidth, contentHeight)

        verScrollBar.size(buttonSize, height)
        verScrollBar.position(width - buttonSize, 0.0)
    }

    protected open fun onMoved() {
        container.y = -verScrollBar.current
    }
}