package com.guang.max.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.roundToInt

/**
 * 瀑布流分割线：
 * 忽略头部
 * 忽略尾部
 * 支持设置头部尾部的分割线大小
 * 支持设置四周的edge大小
 * 支持reverseLayout
 * 支持SpanSizeLookup
 */
class StaggeredDecoration(private val config: Config = Config()) : BaseDecoration() {
  private val TAG = "StaggeredDecoration"

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)
    // 绘制
    // 水平 垂直
    getLayoutManager(parent)?.let {
      when (it.orientation) {
        StaggeredGridLayoutManager.VERTICAL -> drawVertical(c, parent, state)
        StaggeredGridLayoutManager.HORIZONTAL -> drawHorizontal(c, parent, state)
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
//      c.drawRect(rect, paint)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))

      // 右
      val right = bounds.right
      val left = bounds.right - rightSize
      rect.set(left, bounds.top, right, bounds.bottom)
      paint.color = config.color
//      c.drawRect(rect, paint)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))

      // 左
      rect.set(bounds.left, bounds.top, bounds.left + leftSize, bounds.bottom)
      paint.color = config.color
//      c.drawRect(rect, paint)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))

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
//      c.drawRect(rect, paint)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))


      // 上
      paint.color = config.color
      rect.set(bounds.left, bounds.top, bounds.right, bounds.top + topSize)
//      c.drawRect(rect, paint)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))

      // 左
      rect.set(bounds.right - lineSize, bounds.top, bounds.right, bounds.bottom)
      paint.color = config.color
//      c.drawRect(rect, paint)
      config.interceptorDraw(c, rect, paint, position, getOrientation(parent))
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

      // 下
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
        StaggeredGridLayoutManager.VERTICAL -> getVerticalOffsets(outRect, view, parent, state)
        StaggeredGridLayoutManager.HORIZONTAL -> getHorizontalOffsets(outRect, view, parent, state)
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
    val lineSize = config.lineSize
    // 计算得到，每个view的间距
    val topSize = getLeftSize(view, parent, state)
    val bottomSize = getRightSize(view, parent, state)
    // 支持reverseLayout
    val left = if (isRealFirstRow(view, parent, state)) config.leftEdge else lineSize
    // 右侧
    val right = if (isRealLastRow(view, parent, state)) config.rightEdge else 0
    outRect.set(left, topSize, right, bottomSize)

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
    // 获取数据
    val lineSize = config.lineSize
    // 计算得到，每个view的间距
    val leftSize = getLeftSize(view, parent, state)
    val rightSize = getRightSize(view, parent, state)

    // 适配分配 reverseLayout
    val isRealFirstRow = isRealFirstRow(view, parent, state)
    val isRealLastRow = isRealLastRow(view, parent, state)

    // 支持reverseLayout,顶部edge
    val top = if (isRealFirstRow) config.topEdge else lineSize
    // 底部edge
    val bottom = if (isRealLastRow) config.bottomEdge else 0
    outRect.set(leftSize, top, rightSize, bottom)

    // 拦截器，复写父类实现
    val position = parent.getChildLayoutPosition(view)
    config.interceptor(outRect, position, getOrientation(parent))
  }

  /**
   * 是否是第一行
   */
  private fun isFirstRow(
    view: View, parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val it = getLayoutManager(parent) ?: return false
    val lp = getLayoutParams(view) ?: return false
    val position = parent.getChildLayoutPosition(view)
    val spanCount = it.spanCount
    return position == 0 && lp.isFullSpan || (position in 0 until spanCount && !lp.isFullSpan)
  }

  private fun isRealFirstRow(
    view: View, parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    // 获取数据
    val it = getLayoutManager(parent) ?: return false
    val reverseLayout = it.reverseLayout
    val isFirstRow = isFirstRow(view, parent, state)
    val isLastRow = isLastRow(view, parent, state)
    return if (reverseLayout) isLastRow else isFirstRow
  }

  private fun isRealLastRow(
    view: View, parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    // 获取数据
    val it = getLayoutManager(parent) ?: return false
    val reverseLayout = it.reverseLayout
    val isFirstRow = isFirstRow(view, parent, state)
    val isLastRow = isLastRow(view, parent, state)
    return if (reverseLayout) isFirstRow else isLastRow
  }

  /**
   * 是否是最后一行
   */
  private fun isLastRow(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val it = getLayoutManager(parent) ?: return false
    val lp = getLayoutParams(view) ?: return false
    val itemCount = state.itemCount
    val position = parent.getChildLayoutPosition(view)
    val spanCount = it.spanCount
    return (position in (itemCount - spanCount) until itemCount && !lp.isFullSpan) || (position == itemCount - 1 && lp.isFullSpan)
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
    val lp = getLayoutParams(view) ?: return 0
    val position = parent.getChildLayoutPosition(view)
    val spanCount = it.spanCount
    val spanIndex = lp.spanIndex // 当前的列index
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
    val lp = getLayoutParams(view) ?: return 0
    val spanCount = it.spanCount
    val spanSize = if (lp.isFullSpan) spanCount else 1
    val spanIndex = lp.spanIndex // 当前的列index
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
    // 适配分配 reverseLayout
    return isRealFirstRow(view, parent, state)
  }

  /**
   * 是否是第一列的view
   */
  private fun isLeftView(
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ): Boolean {
    val lp = getLayoutParams(view) ?: return false
    val spanIndex = lp.spanIndex
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
    val lp = getLayoutParams(view) ?: return false
    val spanIndex = lp.spanIndex
    val it = getLayoutManager(parent) ?: return false
    val spanCount = it.spanCount
    val spanSize = if (lp.isFullSpan) spanCount else 1
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
    // 适配分配 reverseLayout
    return isRealLastRow(view, parent, state)
  }

  /**
   * 获取当前的layoutManager
   */
  private fun getLayoutManager(parent: RecyclerView) =
    (parent.layoutManager as? StaggeredGridLayoutManager)

  /***
   * 获取布局参数
   */
  private fun getLayoutParams(view: View) =
    (view.layoutParams as? StaggeredGridLayoutManager.LayoutParams)

  /**
   * 获取当前的方向
   */
  private fun getOrientation(parent: RecyclerView) =
    getLayoutManager(parent)?.orientation ?: VERTICAL
}