package com.guang.max.libdecoration.test

import androidx.recyclerview.widget.LinearLayoutManager

/**
 * 配置信息
 */
object Config {
  var linearManagerType = LinearManagerType.Linear
  var spanSize = false
  var span = 2
  var orientation = LinearLayoutManager.VERTICAL
  var stackFormEnd = false
  var reverse = false

  var decoration = Decoration.AG

  enum class Decoration {
    AG, Official, RecyclerItemDecoration, StaggeredGridItemDecoration, GridSpaceItemDecoration, GridItemDecoration
  }

  enum class LinearManagerType {
    Linear, Grid, Stagger
  }
}
