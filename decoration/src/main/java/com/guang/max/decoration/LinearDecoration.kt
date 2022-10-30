package com.guang.max.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * 线性分割线:
 * 忽略头部
 * 忽略尾部
 * 支持设置头部尾部的分割线大小
 * 支持设置四周的edge大小
 * 支持最后一行，无分割线。官方
 * 支持reverseLayout
 */
class LinearDecoration(private val config: Config = Config()) : BaseDecoration() {

  private val TAG = "LinearDecoration"

  init {
    paint.color = config.color
  }

  /**
   * draw -> onDraw -> LinearLayoutManager.onDraw() -> dispatchDraw -> drawChild -> LinearLayoutManager.onDrawOver()
   * 绘制在子view后面
   */
  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)
    // 绘制
    val lm = parent.layoutManager
    if (lm is LinearLayoutManager) {
      when (lm.orientation) {
        LinearLayoutManager.HORIZONTAL -> drawHorizontal(c, parent, state)
        LinearLayoutManager.VERTICAL -> drawVertical(c, parent, state)
      }
      // 绘制边界
      drawEdges(c, parent, state, lm.orientation)
    }
  }

  /**
   * 绘制垂直分割线
   */
  private fun drawVertical(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    c.save()
    val left: Int
    val right: Int
    // 是否clip padding
    if (parent.clipToPadding) {
      left = parent.paddingLeft
      right = parent.width - parent.paddingRight
      c.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
    } else {
      left = 0
      right = parent.width
    }
    // 绘制分割线
    parent.children.forEach {
      // 最后一行不绘制
      if (isFirstView(it, parent, state)) return@forEach
      val intercept = shouldIntercept(it, parent, state)
      val interceptor = getInterceptor(it, parent, state)
      // 拦截后，使用拦截的size，color
      val size = if (intercept) interceptor?.size ?: 0 else config.size
      val color = if (intercept) interceptor?.color ?: 0 else config.color
      parent.getDecoratedBoundsWithMargins(it, bounds)
      val top: Int = bounds.top
      val bottom: Int = top + size - it.translationY.roundToInt()
      rect.set(left, top, right, bottom)
      paint.color = color
      // 拦截绘制
      val position = parent.getChildLayoutPosition(it)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let {
////        c.save()
////        c.clipRect(rect)
//        it(c, rect, paint, position, getOrientation(parent))
////        c.restore()
//      } ?: c.drawRect(rect, paint)
    }
    c.restore()
  }

  /**
   * 绘制水平分割线
   */
  private fun drawHorizontal(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    c.save()
    val top: Int
    val bottom: Int
    // 是否clip padding
    if (parent.clipToPadding) {
      top = parent.paddingTop
      bottom = parent.height - parent.paddingBottom
      c.clipRect(
        parent.paddingLeft,
        top,
        parent.width - parent.paddingRight,
        bottom
      )
    } else {
      top = 0
      bottom = parent.height
    }
    // 绘制分割线
    parent.children.forEach {
      if (isLastView(it, parent, state)) return@forEach
      val intercept = shouldIntercept(it, parent, state)
      val interceptor = getInterceptor(it, parent, state)
      // 拦截后，使用拦截的size，color
      val size = if (intercept) interceptor?.size ?: 0 else config.size
      val color = if (intercept) interceptor?.color ?: 0 else config.color
      parent.getDecoratedBoundsWithMargins(it, bounds)
      val right: Int = bounds.right + it.translationX.roundToInt()
      val left: Int = right - size
      rect.set(left, top, right, bottom)
      paint.color = color
      // 拦截绘制
      val position = parent.getChildLayoutPosition(it)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let {
//        c.save()
//        c.clipRect(rect)
//        it(c, rect, paint, position, getOrientation(parent))
//        c.restore()
//      } ?: c.drawRect(rect, paint)
    }
    c.restore()
  }

  /**
   * 是否应该拦截
   */
  private fun shouldIntercept(
    v: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val position = parent.getChildLayoutPosition(v)
    val interceptor = config.intercept
    return interceptor.find { it.position == position } != null
  }

  /**
   * 获取当前的拦截器
   */
  private fun getInterceptor(
    v: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Config.Intercept? {
    val interceptor = config.intercept
    val position = parent.getChildLayoutPosition(v)
    return interceptor.find { it.position == position }
  }

  /**
   * 绘制边界
   */
  private fun drawEdges(
    c: Canvas,
    parent: RecyclerView,
    state: RecyclerView.State,
    orientation: Int
  ) {
    // 绘制edge
    val edgeColor = config.findEdgeColor()
    var leftEdge = config.leftEdge
    var topEdge = config.topEdge
    var rightEdge = config.rightEdge
    var bottomEdge = config.bottomEdge
    if (config.edges != 0) {
      leftEdge = config.edges
      topEdge = config.edges
      rightEdge = config.edges
      bottomEdge = config.edges
    }
    val left = parent.paddingLeft
    val right = parent.width - parent.paddingRight
    val top = parent.paddingTop
    val bottom = parent.height - parent.paddingBottom
    when (orientation) {
      LinearLayoutManager.VERTICAL -> {
        if (leftEdge != 0) {
          rect.set(left, top, leftEdge, bottom)
          paint.color = edgeColor
          c.drawRect(rect, paint)
        }
        if (rightEdge != 0) {
          rect.set(right - rightEdge, top, right, bottom)
          paint.color = edgeColor
          c.drawRect(rect, paint)
        }
        // 上下边距，动态绘制
        // 绘制分割线
        parent.children.forEach {
          if (isLastView(it, parent, state)) {
            parent.getDecoratedBoundsWithMargins(it, bounds)
            val b: Int = bounds.bottom + it.translationY.roundToInt()
            if (bottomEdge != 0) {
              rect.set(left, b - bottomEdge, right, b)
              paint.color = edgeColor
              c.drawRect(rect, paint)
            }
          }
          if (isFirstView(it, parent, state)) {
            parent.getDecoratedBoundsWithMargins(it, bounds)
            val t: Int = bounds.top - it.translationY.roundToInt()
            if (topEdge != 0) {
              rect.set(left, t, right, t + topEdge)
              paint.color = edgeColor
              c.drawRect(rect, paint)
            }
          }
        }
      }

      LinearLayoutManager.HORIZONTAL -> {
        if (topEdge != 0) {
          rect.set(left, top, right, topEdge)
          paint.color = edgeColor
          c.drawRect(rect, paint)
        }
        if (bottomEdge != 0) {
          rect.set(left, bottom - topEdge, right, bottom)
          paint.color = edgeColor
          c.drawRect(rect, paint)
        }
        // 左右边距，动态绘制
        // 绘制分割线
        parent.children.forEach {
          if (isLastView(it, parent, state)) {
            parent.getDecoratedBoundsWithMargins(it, bounds)
            val r: Int = bounds.right + it.translationX.roundToInt()
            val l: Int = r - rightEdge
            if (rightEdge != 0) {
              rect.set(l, top, r, bottom)
              paint.color = edgeColor
              c.drawRect(rect, paint)
            }
          }
          if (isFirstView(it, parent, state)) {
            parent.getDecoratedBoundsWithMargins(it, bounds)
            val l: Int = bounds.left - it.translationX.roundToInt()
            if (leftEdge != 0) {
              rect.set(l, top, l + leftEdge, bottom)
              paint.color = edgeColor
              c.drawRect(rect, paint)
            }
          }
        }
      }
    }
  }

  /**
   * 分割线，viewHolder的insetOffset
   */
  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    val lm = parent.layoutManager
    if (lm is LinearLayoutManager) {
      when (lm.orientation) {
        LinearLayoutManager.HORIZONTAL -> {
          getHorizontalOffsets(outRect, view, parent, state)
        }
        LinearLayoutManager.VERTICAL -> {
          getVerticalOffsets(outRect, view, parent, state)
        }
      }
    }
  }

  /**
   * 水平分割线的间距
   */
  private fun getHorizontalOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    var leftEdge = config.leftEdge
    var topEdge = config.topEdge
    var rightEdge = config.rightEdge
    var bottomEdge = config.bottomEdge
    if (config.edges != 0) {
      leftEdge = config.edges
      topEdge = config.edges
      rightEdge = config.edges
      bottomEdge = config.edges
    }
    val intercept = shouldIntercept(view, parent, state)
    val interceptor = getInterceptor(view, parent, state)
    val size = if (intercept) interceptor?.size ?: 0 else config.size
    val left = if (isFirstView(view, parent, state)) leftEdge else 0
    val right = if (isLastView(view, parent, state)) rightEdge else size
    outRect.set(left, topEdge, right, bottomEdge)

    // 拦截器，复写父类实现
    val position = parent.getChildLayoutPosition(view)
    config.interceptor(outRect, position, getOrientation(parent))
  }

  /**
   * 垂直分割线的间距
   */
  private fun getVerticalOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    var leftEdge = config.leftEdge
    var topEdge = config.topEdge
    var rightEdge = config.rightEdge
    var bottomEdge = config.bottomEdge
    if (config.edges != 0) {
      leftEdge = config.edges
      topEdge = config.edges
      rightEdge = config.edges
      bottomEdge = config.edges
    }
    val intercept = shouldIntercept(view, parent, state)
    val interceptor = getInterceptor(view, parent, state)
    val size = if (intercept) interceptor?.size ?: 0 else config.size
    val top = if (isFirstView(view, parent, state)) topEdge else size
    val bottom = if (isLastView(view, parent, state)) bottomEdge else 0
    outRect.set(leftEdge, top, rightEdge, bottom)

    // 拦截器，复写父类实现
    val position = parent.getChildLayoutPosition(view)
    config.interceptor(outRect, position, getOrientation(parent))
  }

  /**
   * 是否是最后一个view
   */
  private fun isLastView(view: View, parent: RecyclerView, state: RecyclerView.State): Boolean {
    val lm = getLayoutManager(parent)
    val reverse = lm?.reverseLayout ?: false
    val lastCount = if (reverse) 0 else state.itemCount - 1
    return lastCount == parent.getChildLayoutPosition(view)
  }

  /**
   * 是否是最后一个view
   */
  private fun isFirstView(view: View, parent: RecyclerView, state: RecyclerView.State): Boolean {
    val lm = getLayoutManager(parent)
    val reverse = lm?.reverseLayout ?: false
    val lastCount = if (reverse) state.itemCount - 1 else 0
    return lastCount == parent.getChildLayoutPosition(view)
  }

  /**
   * 获取当前的layoutManager
   */
  private fun getLayoutManager(parent: RecyclerView) =
    (parent.layoutManager as? LinearLayoutManager)

  /**
   * 获取当前的方向
   */
  private fun getOrientation(parent: RecyclerView) =
    getLayoutManager(parent)?.orientation ?: VERTICAL

}