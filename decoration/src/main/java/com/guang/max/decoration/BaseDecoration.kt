package com.guang.max.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

const val VERTICAL = RecyclerView.VERTICAL
const val HORIZONTAL = RecyclerView.HORIZONTAL

/**
 * 绘制拦截器
 * @param rect 表明绘制的范围
 * @param position 绘制的item位置
 * @param direction 分割线的方向。水平 & 竖直
 */
typealias InterceptDrawListener = (c: Canvas, rect: Rect, paint: Paint, position: Int, direction: Int) -> Boolean

/**
 * 分割线拦截器
 * @param outRect 表明绘制的范围
 * @param position 绘制的item位置
 * @param direction 分割线的方向。水平 & 竖直
 */
typealias InterceptOffsetListener = (outRect: Rect, position: Int, direction: Int) -> Boolean

open class BaseDecoration : RecyclerView.ItemDecoration() {

  protected val bounds = Rect()
  protected val rect = Rect()

  /**
   * 默认画笔
   */
  val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    color = Color.TRANSPARENT
  }

  /**
   * 配置信息
   */
  data class Config(
    // 分割线大小
    var size: Int = 0,
    var lineSize: Int = 0, // grid & stag 纵向分割线
    // 分割线颜色
    var color: Int = Color.TRANSPARENT,
    // 忽略的头部分割线大小
    var ignoreHeader: Int = 0,
    // 忽略的尾部分割线大小
    var ignoreFooter: Int = 0,
    // 头部分割线大小
    var headerSize: Int = 0,
    // 尾部分割线大小
    var footerSize: Int = 0,
    // 头部分割线大小
    var headerColor: Int = INVALID_COLOR,
    // 尾部分割线大小
    var footerColor: Int = INVALID_COLOR,
    // 头部数目
    var headers: Int = 0,
    // 尾部数目
    var footers: Int = 0,
    var intercept: List<Intercept> = emptyList(),
    var interceptDrawListener: InterceptDrawListener? = null,
    var interceptOffsetListener: InterceptOffsetListener? = null,
    // 四周的边距
    var edgeColor: Int = INVALID_COLOR,
    var topEdge: Int = 0,
    var leftEdge: Int = 0,
    var rightEdge: Int = 0,
    var bottomEdge: Int = 0,
    var edges: Int = 0
  ) {
    companion object {
      const val INVALID_COLOR = Color.TRANSPARENT
    }

    /**
     * 拦截器，用来替换某一个position的分割线
     */
    data class Intercept(
      val position: Int = 0,
      val size: Int = 0,
      val color: Int = INVALID_COLOR
    )

    val offsetListener: () -> Unit = {}
    val drawListener: () -> Unit = {}

    class InterceptCopy(
//      val interceptOffset: offsetListener
    )

    /**
     * 有头部，尾部
     */
    fun hasHeaders() = headers != 0
    fun hasFooter() = footers != 0

    fun findEdgeColor() = if (edgeColor == INVALID_COLOR) color else edgeColor
    fun findHeaderColor() = if (headerColor == INVALID_COLOR) color else headerColor
    fun findFooterColor() = if (footerColor == INVALID_COLOR) color else footerColor

    /**
     * 拦截器
     * 任何事情都不做，直接传递父view
     */
    fun interceptor(outRect: Rect, position: Int, direction: Int) {
      val interceptor = interceptOffsetListener
      if (interceptor != null && interceptor(outRect, position, direction)) {
        // do nothing
      }
    }

    /**
     * 拦截器--绘制
     */
    fun interceptorDraw(c: Canvas, rect: Rect, paint: Paint, position: Int, direction: Int) {
      val interceptor = interceptDrawListener
      if (interceptor != null && interceptor(c, rect, paint, position, direction)) {
        // do nothing
      } else {
        // 原来的绘制
        c.drawRect(rect, paint)
      }
    }
  }
}