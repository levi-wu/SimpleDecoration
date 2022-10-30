package com.guang.max.libdecoration

object Data {

  fun getData(): List<String> {
    // adapter
    val data = mutableListOf<String>()
    for (i in 0..60) {
      data.add(i.toString())
    }
    return data
  }
}