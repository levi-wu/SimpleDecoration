package com.guang.max.libdecoration

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.guang.max.decoration.SimpleDecoration
import com.guang.max.libdecoration.databinding.ActivityAgBinding
import com.guang.max.libdecoration.test.*
import kotlin.math.max

class SimpleDecorationActivity : AppCompatActivity() {

  private lateinit var binding: ActivityAgBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAgBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initController()
  }

  private fun initController() {
    val behavior = BottomSheetBehavior.from(binding.controller)
    behavior.apply {
      peekHeight = 300
      isHideable = false
    }
    binding.linearType.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.cb_layout_linear -> {
          Config.linearManagerType = Config.LinearManagerType.Linear
        }
        R.id.cb_layout_grid -> {
          Config.linearManagerType = Config.LinearManagerType.Grid
        }
        R.id.cb_layout_stagger -> {
          Config.linearManagerType = Config.LinearManagerType.Stagger
        }
      }
      initListener()
    }
    binding.linearType.check(R.id.cb_layout_stagger)
    binding.cbSpan.setOnCheckedChangeListener { buttonView, isChecked ->
      val spanSize = if (isChecked) 4 else 2
      Config.span = spanSize
      initListener()
    }
    binding.cbSpan.isChecked = false
    binding.cbSpanSize.setOnCheckedChangeListener { buttonView, isChecked ->
      val spanSize = if (isChecked) 2 else 1
      Config.spanSize = isChecked
      initListener()
    }
    binding.cbSpanSize.isChecked = false
    binding.cbOrientation.setOnCheckedChangeListener { buttonView, isChecked ->
      val orientation =
        if (isChecked) LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL
      Config.orientation = orientation
      binding.cbOrientation.text = if (isChecked) "水平" else "垂直"
      initListener()
    }
    binding.cbOrientation.isChecked = false

    binding.cbStack.setOnCheckedChangeListener { buttonView, isChecked ->
      Config.stackFormEnd = isChecked
      initListener()
    }
    binding.cbStack.isChecked = false

    binding.cbReverse.setOnCheckedChangeListener { buttonView, isChecked ->
      Config.reverse = isChecked
      initListener()
    }
    binding.cbReverse.isChecked = false

    // 分割线
    binding.rgDec.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.cb_ag -> {
          Config.decoration = Config.Decoration.AG
        }
        R.id.cb_offical -> {
          Config.decoration = Config.Decoration.Official
        }
        R.id.cb_recycler_item -> {
          Config.decoration = Config.Decoration.RecyclerItemDecoration
        }
        R.id.cb_stag_Grid -> {
          Config.decoration = Config.Decoration.StaggeredGridItemDecoration
        }
        R.id.cb_grid_space -> {
          Config.decoration = Config.Decoration.GridSpaceItemDecoration
        }
        R.id.cb_grid_item -> {
          Config.decoration = Config.Decoration.GridItemDecoration
        }
      }
      initListener()
    }
    binding.rgDec.check(R.id.cb_ag)
  }


  private fun initListener() {
    binding.recycler.apply {
      // layout manager
      layoutManager = when (Config.linearManagerType) {
        Config.LinearManagerType.Linear -> LinearLayoutManager(
          context,
          Config.orientation,
          Config.reverse
        ).apply {
          stackFromEnd = Config.stackFormEnd
        }
        Config.LinearManagerType.Grid -> GridLayoutManager(
          context,
          Config.span,
          Config.orientation,
          Config.reverse
        ).apply {
//          stackFromEnd = Config.stackFormEnd
          spanSizeLookup = if (Config.spanSize) {
            object : GridLayoutManager.SpanSizeLookup() {
              override fun getSpanSize(position: Int): Int {
                return max(1, position % spanCount)
              }
            }
          } else {
            GridLayoutManager.DefaultSpanSizeLookup()
          }
        }
        Config.LinearManagerType.Stagger -> StaggeredGridLayoutManager(
          Config.span,
          Config.orientation
        ).apply {
//          stackFromEnd = Config.stackFormEnd
          reverseLayout = Config.reverse
        }
      }

      // 清除所有的分割线
      for (i in 0 until itemDecorationCount) {
        removeItemDecorationAt(i)
      }
      // 重新添加
      val item = when (Config.decoration) {
        Config.Decoration.AG -> {
          val size = 10.dp
          val decoration = SimpleDecoration
            .Builder()
            .setSize(size)
            .setLineSize(size * 2)
            .setColor(Color.BLACK)
            .setHorizontalEdge(size * 2)
            .setVerticalEdge(size * 2)
            .setEdgeColor(Color.RED)
//            .setInterceptOffsetListener { outRect, position, direction ->
//              if (position == 1) {
//                // 底部间距重置
//                outRect.top = 100
//              }
//              position == 1
//            }
//            .setInterceptDraw { c, rect, paint, position, direction ->
//              if (position == 1) {
//                rect.bottom = rect.top + 100
//                val bit = BitmapFactory.decodeResource(resources, R.drawable.labi)
//                c.drawBitmap(bit, null, rect, paint)
//              }
//              position == 1
//            }
            .build()
          decoration
        }
        Config.Decoration.Official -> {
          DividerItemDecoration(context, Config.orientation).apply {
            getDrawable(R.drawable.decoration)?.let { setDrawable(it) }
          }
        }
        Config.Decoration.RecyclerItemDecoration -> {
          RecyclerItemDecoration(0, 0, 0, 10.dp)
        }
        Config.Decoration.GridItemDecoration -> {
          GridItemDecoration(10.dp)
        }
        Config.Decoration.GridSpaceItemDecoration -> {
          GridSpaceItemDecoration(Config.span, 10.dp, 10.dp)
        }
        Config.Decoration.StaggeredGridItemDecoration -> {
          StaggeredGridItemDecoration(10.dp, Config.span, 10.dp)
        }
      }
      addItemDecoration(item)

      // adapter
      when (Config.orientation) {
        RecyclerView.HORIZONTAL -> {
          adapter = SimpleDecorationHorizontalAdapter().apply {
            setData(Data.getData())
          }
        }
        RecyclerView.VERTICAL -> {
          adapter = SimpleDecorationVerticalAdapter().apply {
            setData(Data.getData())
          }
        }
      }
    }
  }

  private fun initRecycler() {
    val data = mutableListOf<String>()

    for (i in 0..60) {
      data.add(i.toString())
    }
    binding.recycler.apply {
//      adapter = InnerAdapter().apply {
//        setData(data)
//      }
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
//        stackFromEnd = true
      }
//      layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, true).apply {
//        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//          override fun getSpanSize(position: Int): Int {
//            return max(1, position % spanCount)
//          }
//        }
//      }
      layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL).apply {
        reverseLayout = false
      }

      val size =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
          .toInt()
      val decoration = SimpleDecoration
        .Builder()
        .setSize(size)
        .setColor(Color.BLUE)
//        .setEdges(size * 2)
        .setRightEdge(size * 2)
        .setLeftEdge(size * 2)
        .setEdgeColor(Color.CYAN)
        .setLineSize(size * 2)
//        .setIntercept(
//          listOf(
//            BaseDecoration.Config.Intercept(0, 0, Color.YELLOW),
//            BaseDecoration.Config.Intercept(1, size * 2, Color.YELLOW),
//            BaseDecoration.Config.Intercept(data.size - 2, size * 2, Color.YELLOW),
//            BaseDecoration.Config.Intercept(data.size - 1, size * 2, Color.YELLOW)
//          )
//        )
//        .setInterceptDraw { c, rect, paint, position, direction ->
////          if (position in 0..6) {
//          paint.color = Color.BLACK
//          paint.strokeWidth = 5f
//          when (direction) {
//            HORIZONTAL -> {
//              c.drawLine(
//                rect.left.toFloat(),
//                rect.exactCenterY(),
//                rect.right.toFloat(),
//                rect.exactCenterY(),
//                paint
//              )
//            }
//            VERTICAL -> {
//              c.drawLine(
//                rect.exactCenterX(),
//                rect.top.toFloat(),
//                rect.exactCenterX(),
//                rect.bottom.toFloat(),
//                paint
//              )
//            }
//          }
////          }
//        }
        .build()
      addItemDecoration(
        decoration
      )
//      addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
//        setDrawable(getDrawable(R.drawable.decoration))
//      })
    }
  }


  private val Int.dp: Int
    get() = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      this.toFloat(),
      resources.displayMetrics
    ).toInt()
}

