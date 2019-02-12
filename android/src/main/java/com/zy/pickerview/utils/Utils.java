package com.zy.pickerview.utils;

import com.facebook.react.bridge.ReadableArray;

public class Utils {
  public static int[] getColor(ReadableArray array) {
    int[] colors = new int[4];
    for (int i = 0; i < array.size(); i++) {
      switch (i) {
        case 0:
        case 1:
        case 2:
          colors[i] = array.getInt(i);
          break;
        case 3:
          colors[i] = (int) (array.getDouble(i) * 255);
          break;
        default:
          break;
      }
    }
    return colors;
  }
}
