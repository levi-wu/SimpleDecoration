package com.guang.max.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * 网格分割线：
 * 忽略头部
 * 忽略尾部
 * 支持设置头部尾部的分割线大小
 * 支持设置四周的edge大小
 * 支持reverseLayout
 * 支持SpanSizeLookup
 */
class GridDecoration(private val config: Config = Config()) : BaseDecoration() {

  private val TAG = "GridDecoration"

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)
    // 绘制
    // 水平 垂直
    getLayoutManager(parent)?.let {
      when (it.orientation) {
        GridLayoutManager.VERTICAL -> drawVertical(c, parent, state)
        GridLayoutManager.HORIZONTAL -> drawHorizontal(c, parent, state)
      }
    }
  }

  /**
   * 绘制垂直分割线
   */
  private fun drawVertical(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    c.save()
    // 绘制分割线
    parent.children.forEach {
      val leftSize = getLeftSize(it, parent, state)
      val rightSize = getRightSize(it, parent, state)
      val position = parent.getChildLayoutPosition(it)
      parent.getDecoratedBoundsWithMargins(it, bounds)

      // 上
      val top = bounds.top
      val bottom = top + config.lineSize
      rect.set(bounds.left, top, bounds.right, bottom)
      paint.color = config.color
      // 拦截，右侧 & 下侧，position
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let { intercept ->
//        if (isLastView(it, parent, state)) return@let
//        c.save()
//        c.clipRect(rect)
//        intercept(c, rect, paint, position, HORIZONTAL)
//        c.restore()
//      } ?: c.drawRect(rect, paint)

      // 右
      val right = bounds.right
      val left = bounds.right - rightSize
      rect.set(left, bounds.top, right, bounds.bottom)
      paint.color = config.color
      // 拦截，右侧 & 下侧，position
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let { intercept ->
//        if (isRightView(it, parent, state)) return@let
//        // 计算完整分割线
//        c.save()
//        c.clipRect(rect)
//        rect.right = rect.left + config.size
//        intercept(c, rect, paint, position, VERTICAL)
//        c.restore()
//      } ?: c.drawRect(rect, paint)

      // 左
      rect.set(bounds.left, bounds.top, bounds.left + leftSize, bounds.bottom)
      paint.color = config.color
      // 拦截，右侧 & 下侧，position
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let {
//      } ?: c.drawRect(rect, paint)

      /**
       * edges
       */
      // 左
      if (isLeftView(it, parent, state)) {
//        rect.set(bounds.left, bounds.top, bounds.left + leftSize, bounds.bottom)
        rect.set(bounds.left, 0, bounds.left + leftSize, parent.height)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }

      // 右
      if (isRightView(it, parent, state)) {
//        rect.set(bounds.right - rightSize, bounds.top, bounds.right, bounds.bottom)
        rect.set(bounds.right - rightSize, 0, bounds.right, parent.height)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }

      // 上
      if (isTopView(it, parent, state)) {
//        rect.set(bounds.left, bounds.top, bounds.right, bounds.top + config.topEdge)
        rect.set(0, bounds.top, parent.width, bounds.top + config.topEdge)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }

      // 下
      if (isLastView(it, parent, state)) {
//        rect.set(bounds.left, bounds.bottom - config.bottomEdge, bounds.right, bounds.bottom)
        rect.set(0, bounds.bottom - config.bottomEdge, parent.width, bounds.bottom)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }
    }
    c.restore()
  }

  /**
   * 绘制水平分割线
   */
  private fun drawHorizontal(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    c.save()
    // 绘制分割线
    parent.children.forEach {
      parent.getDecoratedBoundsWithMargins(it, bounds)
      // 计算得到，每个view的间距
      val topSize = getLeftSize(it, parent, state)
      val bottomSize = getRightSize(it, parent, state)
      val position = parent.getChildLayoutPosition(it)
      // 尺寸
      val size = config.size
      val lineSize = config.lineSize

      // 下
      rect.set(bounds.left, bounds.bottom - bottomSize, bounds.right, bounds.bottom)
      paint.color = config.color
      // 拦截，右侧 & 下侧，position
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let { intercept ->
//        if (isRightView(it, parent, state)) return@let
//        c.save()
//        c.clipRect(rect)
//        rect.bottom = rect.top + config.size
//        intercept(c, rect, paint, position, HORIZONTAL)
//        c.restore()
//      } ?: c.drawRect(rect, paint)

      // 上
      paint.color = config.color
      rect.set(bounds.left, bounds.top, bounds.right, bounds.top + topSize)
      // 拦截，右侧 & 下侧，position
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let {
//      } ?: c.drawRect(rect, paint)

      // 右
      rect.set(bounds.right - lineSize, bounds.top, bounds.right, bounds.bottom)
      paint.color = config.color
      // 拦截，右侧 & 下侧，position
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
//      config.interceptDrawListener?.let { intercept ->
//        if (isLastView(it, parent, state)) return@let
//        c.save()
//        c.clipRect(rect)
//        intercept(c, rect, paint, position, VERTICAL)
//        c.restore()
//      } ?: c.drawRect(rect, paint)

      /**
       * edges
       */
      // 上
      if (isLeftView(it, parent, state)) {
        //        rect.set(bounds.left, bounds.top, bounds.right, bounds.top + config.topEdge)
        rect.set(0, bounds.top, parent.width, bounds.top + config.topEdge)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }
//
      // 下
      if (isRightView(it, parent, state)) {
        //        rect.set(bounds.left, bounds.bottom - config.bottomEdge, bounds.right, bounds.bottom)
        rect.set(0, bounds.bottom - config.bottomEdge, parent.width, bounds.bottom)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }

      // 左
      if (isTopView(it, parent, state)) {
        //        rect.set(bounds.left, bounds.top, bounds.left + leftSize, bounds.bottom)
        rect.set(bounds.left, 0, bounds.left + config.leftEdge, parent.height)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }

      // 右
      if (isLastView(it, parent, state)) {
        //        rect.set(bounds.right - rightSize, bounds.top, bounds.right, bounds.bottom)
        rect.set(bounds.right - config.rightEdge, 0, bounds.right, parent.height)
        paint.color = config.edgeColor
        c.drawRect(rect, paint)
      }
    }
    c.restore()
  }

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    // 水平 垂直
    getLayoutManager(parent)?.let {
      when (it.orientation) {
        GridLayoutManager.VERTICAL -> getVerticalOffsets(outRect, view, parent, state)
        GridLayoutManager.HORIZONTAL -> getHorizontalOffsets(outRect, view, parent, state)
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
//    val intercept = shouldIntercept(view, parent, state)
//    val interceptor = getInterceptor(view, parent, state)
//    val size = if (intercept) interceptor?.size ?: 0 else config.size
//    val left = if (isFirstView(view, parent, state)) leftEdge else 0
//    val right = if (isLastView(view, parent, state)) rightEdge else size
//    outRect.set(left, topEdge, right, bottomEdge)

    val it = getLayoutManager(parent) ?: return
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanSize = spanSizeLookup.getSpanSize(position)  // 当前的占据大小
    val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount) // 当前的行 index
    val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount) // 第几列
    val reverseLayout = it.reverseLayout
    val itemCount = state.itemCount

    val halfSize = config.size
    val lineSize = config.lineSize

    // 计算得到，每个view的间距
    val topSize = getLeftSize(view, parent, state)
    val bottomSize = getRightSize(view, parent, state)

    // 支持SpanSizeLookup
    val isEndSpanIndex = (spanCount - (spanIndex + 1)) < spanSize
    val bottom = if (isEndSpanIndex) 0 else lineSize
    // 支持reverseLayout
    val lastSpanGroupIndex = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount)
    val isFirstSpanIndex = if (reverseLayout) lastSpanGroupIndex else 0
    val left = if (spanGroupIndex == isFirstSpanIndex) config.leftEdge else lineSize
    // 右侧
    val isLast = if (reverseLayout) 0 else lastSpanGroupIndex
    val right = if (spanGroupIndex == isLast) config.rightEdge else 0
    outRect.set(left, topSize, right, bottomSize)

    // 拦截器，复写父类实现
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
    // 获取数据
    val it = getLayoutManager(parent) ?: return
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanSize = spanSizeLookup.getSpanSize(position)  // 当前的占据大小
    val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount) // 当前的列index
    val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)  // 第几行
    val reverseLayout = it.reverseLayout
    val itemCount = state.itemCount
    val lastSpanGroupIndex = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount)

    val lineSize = config.lineSize

    // 计算得到，每个view的间距
    val leftSize = getLeftSize(view, parent, state)
    val rightSize = getRightSize(view, parent, state)
    // 支持reverseLayout,顶部edge
    val isFirstSpanIndex = if (reverseLayout) lastSpanGroupIndex else 0
    val top = if (spanGroupIndex == isFirstSpanIndex) config.topEdge else lineSize
    // 底部edge
    val isLast = if (reverseLayout) 0 else lastSpanGroupIndex
    val bottom = if (spanGroupIndex == isLast) config.bottomEdge else 0
    outRect.set(leftSize, top, rightSize, bottom)

    // 拦截器，复写父类实现
    config.interceptor(outRect, position, getOrientation(parent))
  }

  /**
   * 获取左边距
   */
  private fun getLeftSize(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Int {
    val it = getLayoutManager(parent) ?: return 0
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount) // 当前的列index
    val size = config.size
    // item的大小
    val item = getItemSize(parent)
//    val fl = config.leftEdge  // 0  le
//    val fr = item - fl
//    val sl = config.size - fr // 1 size - item + le
//    val sr = item - sl
//    val tl = config.size - sr // 2  size - item + size - item + le
//    val tr = item - tl
    return config.leftEdge + (size - item) * spanIndex
  }

  /**
   * 获取右边距
   */
  private fun getRightSize(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Int {
    val it = getLayoutManager(parent) ?: return 0
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanSize = spanSizeLookup.getSpanSize(position)  // 当前的占据大小
    val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount) // 当前的列index
    // item占据的大小
    val item = getItemSize(parent)
    // 支持SpanSizeLookup
    val isEndSpanIndex = (spanCount - (spanIndex + 1)) < spanSize
    return if (isEndSpanIndex) config.rightEdge else item - getLeftSize(view, parent, state)
  }

  /**
   * 获取水平方向的item的总尺寸
   */
  private fun getItemSize(parent: RecyclerView): Int {
    val it = getLayoutManager(parent) ?: return 0
    val spanCount = it.spanCount
    val allSize = config.size * (spanCount - 1) + config.leftEdge + config.rightEdge
    return (1f * allSize / spanCount).roundToInt()
  }

  /**
   * 是否是第一行的view
   */
  private fun isTopView(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val it = getLayoutManager(parent) ?: return false
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)  // 第几行
    val reverseLayout = it.reverseLayout
    val itemCount = state.itemCount
    val lastSpanGroupIndex = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount)
    val isFirstSpanIndex = if (reverseLayout) lastSpanGroupIndex else 0
    return spanGroupIndex == isFirstSpanIndex
  }

  /**
   * 是否是第一列的view
   */
  private fun isLeftView(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val it = getLayoutManager(parent) ?: return false
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount) // 当前的列index
    return spanIndex == 0
  }

  /**
   * 是否是最后一列的view
   */
  private fun isRightView(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val it = getLayoutManager(parent) ?: return false
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanSize = spanSizeLookup.getSpanSize(position)  // 当前的占据大小
    val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount) // 当前的列index
    // 支持SpanSizeLookup
    return (spanCount - (spanIndex + 1)) < spanSize
  }

  /**
   * 是否是最后一行的view
   */
  private fun isLastView(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val it = getLayoutManager(parent) ?: return false
    val position = parent.getChildLayoutPosition(view)
    val spanSizeLookup = it.spanSizeLookup
    val spanCount = it.spanCount
    val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)  // 第几行
    val reverseLayout = it.reverseLayout
    val itemCount = state.itemCount
    val lastSpanGroupIndex = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount)
    val isLast = if (reverseLayout) 0 else lastSpanGroupIndex
    return spanGroupIndex == isLast
  }

  /**
   * 获取当前的layoutManager
   */
  private fun getLayoutManager(parent: RecyclerView) =
    (parent.layoutManager as? GridLayoutManager)

  /**
   * 获取当前的方向
   */
  private fun getOrientation(parent: RecyclerView) =
    getLayoutManager(parent)?.orientation ?: VERTICAL
}