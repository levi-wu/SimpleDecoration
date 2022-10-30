package com.guang.max.libdecoration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.guang.max.libdecoration.databinding.ItemDecorationHorizontalBinding
import com.guang.max.libdecoration.databinding.ItemDecorationVerticalBinding

/**
 * 竖直适配器
 */
class SimpleDecorationVerticalAdapter : RecyclerView.Adapter<SimpleDecorationVerticalAdapter.VH>() {
  private val data = mutableListOf<String>()

  fun setData(data: List<String>) {
    this.data.addAll(data)
  }

  inner class VH(item: View) : RecyclerView.ViewHolder(item) {
    val binding = ItemDecorationVerticalBinding.bind(item)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_decoration_vertical, parent, false))
  }

  override fun getItemCount(): Int {
    return data.size
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    val lp = holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams
    holder.binding.root.apply {
      layoutParams = this.layoutParams.apply {
        if (lp != null) {
          height = 300 + (position % 3) * 100
        }
      }
    }
//    lp?.isFullSpan = position == 0
    holder.binding.tv.text = position.toString()
  }
}

/**
 * 水平适配器
 */
class SimpleDecorationHorizontalAdapter : RecyclerView.Adapter<SimpleDecorationHorizontalAdapter.VH>() {
  private val data = mutableListOf<String>()

  fun setData(data: List<String>) {
    this.data.addAll(data)
  }

  inner class VH(item: View) : RecyclerView.ViewHolder(item) {
    val binding = ItemDecorationHorizontalBinding.bind(item)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_decoration_horizontal, parent, false))
  }

  override fun getItemCount(): Int {
    return data.size
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    val lp = holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams
    holder.binding.root.apply {
      layoutParams = this.layoutParams.apply {
        if (lp != null) {
          height = 300 + (position % 3) * 100
        }
      }
    }
//    lp?.isFullSpan = position == 0
    holder.binding.tv.text = position.toString()
  }
}