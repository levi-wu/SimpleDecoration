package com.guang.max.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 简单分割线
 * usage:
 * val decoration = SimpleDecoration
 * .Builder()
 * .setSize(size)
 * .setColor(Color.BLUE)
 * .setEdges(size * 2)
 * .setEdgeColor(Color.CYAN)
 * .setLineSize(size * 2)
 * .build()
 */
class SimpleDecoration private constructor(private val config: Config = Config()) : BaseDecoration() {

  /**
   * 获取代理manager
   */
  private fun getProxyManager(parent: RecyclerView) = when (parent.layoutManager) {
    is GridLayoutManager -> GridDecoration(config)
    is LinearLayoutManager -> LinearDecoration(config)
    is StaggeredGridLayoutManager -> StaggeredDecoration(config)
    else -> null
  }

  /**
   * 绘制
   */
  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    getProxyManager(parent)?.onDraw(c, parent, state)
  }

  /**
   * 设置边距
   */
  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    getProxyManager(parent)?.let {
      it.getItemOffsets(outRect, view, parent, state)
    } ?: super.getItemOffsets(outRect, view, parent, state)
  }

  /**
   * 构造者
   */
  class Builder {
    // 配置
    private val config = Config()

    /**
     * 设置分割线尺寸
     */
    fun setSize(size: Int) = apply {
      config.size = size
      config.lineSize = size
    }

    /**
     * 设置分割线颜色
     */
    fun setColor(color: Int) = apply { config.color = color }

    /**
     * GridLayoutManager & StaggeredGridLayoutManager 生效
     * Vertical：表示行之间的间距
     * Horizontal：表示列之间的间距
     */
    fun setLineSize(size: Int) = apply { config.lineSize = size }

    /**
     * 设置边距的颜色
     */
    fun setEdgeColor(color: Int) = apply { config.edgeColor = color }

    /**
     * 设置四周的边距
     */
    fun setEdges(edge: Int) = apply {
      config.edges = edge
      // 赋值
      setTopEdge(edge)
      setLeftEdge(edge)
      setRightEdge(edge)
      setBottomEdge(edge)
    }

    /**
     * 设置上边距
     */
    fun setTopEdge(edge: Int) = apply { config.topEdge = edge }

    /**
     * 设置左边距
     */
    fun setLeftEdge(edge: Int) = apply { config.leftEdge = edge }

    /**
     * 设置右边距
     */
    fun setRightEdge(edge: Int) = apply { config.rightEdge = edge }

    /**
     * 设置下边距
     */
    fun setBottomEdge(edge: Int) = apply { config.bottomEdge = edge }

    /**
     * 设置水平边距
     */
    fun setHorizontalEdge(edge: Int) = apply {
      setLeftEdge(edge)
      setRightEdge(edge)
    }

    /**
     * 设置竖直边距
     */
    fun setVerticalEdge(edge: Int) = apply {
      setTopEdge(edge)
      setBottomEdge(edge)
    }

//    /**
//     * 拦截器
//     */
//    fun setIntercept(intercept: List<Config.Intercept>) = apply { config.intercept = intercept }

    /**
     * 拦截绘制
     */
    fun setInterceptDraw(intercept: InterceptDrawListener) =
      apply { config.interceptDrawListener = intercept }

    /**
     * 重置分割线间距偏移量拦截器
     */
    fun setInterceptOffsetListener(intercept: InterceptOffsetListener) =
      apply { config.interceptOffsetListener = intercept }

    /**
     * 创建
     */
    fun build() = SimpleDecoration(config)
  }
}