package com.zy.pickerview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.zy.pickerview.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import static android.graphics.Color.argb;

public class DateTimePickerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  /**
   * Minimum date supported by {@link }, 01 Jan 1900
   */
  private static final long DEFAULT_MIN_DATE = -2208988800001l;

  private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private static final String REACT_CLASS = "ZYDateTimePicker";

  static final String ACTION_DATE_SET = "dateSetAction";

  /* package */ static final String ARG_DATE = "date";
  /* package */ static final String ARG_MINDATE = "minDate";
  /* package */ static final String ARG_MAXDATE = "maxDate";
  /* package */ static final String ARG_MODE = "mode";
  /* package */ static final String ARG_TITLE = "title";
  /* package */ static final String ARG_FORMAT = "format";

  private static final String OK_TEXT = "okText";

  private static final String DISMISS_TEXT = "dismissText";

  private static final String TITLE_COLOR = "titleColor";

  private static final String CONFIRM_BTN_COLOR = "confirmBtnColor";

  private static final String CANCEL_BTN_COLOR = "cancelBtnColor";

  private static final String BG_COLOR = "bgColor";

  private static final String TITLE_BG_COLOR = "titleBgColor";

  private static final String DIVIDER_COLOR = "dividerColor";

  private static final String TEXT_CENTER_COLOR = "textCenterColor";

  private static final String TEXT_OUT_COLOR = "textOutColor";

  private static final String EVENT_KEY_CONFIRM = "confirm";

  private static final String PICKER_EVENT_NAME = "timePickerEvent";

  private TimePickerView timePickerView;

  private TimePickerBuilder timePickerBuilder;

  private WritableMap resultMap;

  private ReadableMap options;

  public DateTimePickerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addLifecycleEventListener(this);
  }

  private class DatePickerListener implements OnTimeSelectListener {

    private final Bundle margs;

    public DatePickerListener(final Bundle args) {
      margs = args;
    }

    @Override
    public void onTimeSelect(Date date, View v) {
      if (getReactApplicationContext().hasActiveCatalystInstance()) {
        resultMap = Arguments.createMap();
        String format = DEFAULT_FORMAT;
        if(margs != null && margs.containsKey(ARG_FORMAT)) {
          format = margs.getString(ARG_FORMAT);
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        resultMap.putString("action", ACTION_DATE_SET);
        resultMap.putString("date", dateString);
        resultMap.putInt("year", year);
        resultMap.putInt("month", month);
        resultMap.putInt("day", day);

        commonEvent(EVENT_KEY_CONFIRM);
      }
    }
  }

  @Override
  public void onHostResume() {

  }

  @Override
  public void onHostPause() {
    hide();
    timePickerView = null;
  }

  @Override
  public void onHostDestroy() {
    hide();
    timePickerView = null;
  }

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  private void build() {
    final Activity activity = getCurrentActivity();
    final Bundle args = createFragmentArguments(options);
    //时间选择器
    final DatePickerListener listener = new DatePickerListener(args);

    timePickerBuilder = new TimePickerBuilder(activity, listener);
    timePickerBuilder.setLineSpacingMultiplier(1.8f); //设置两横线之间的间隔倍数
    setting(options);
    setType(args);
    setTitle(args);
    setSelectDate(args);
    setRangDate(args);
    timePickerView = timePickerBuilder.build();
  }

  @ReactMethod
  public void open(@Nullable final ReadableMap options) {
    this.options = options;
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        build();
      }
    });

  }

  @ReactMethod
  public void show() {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (timePickerView == null) {
          build();
        }
        if (!timePickerView.isShowing()) {
          timePickerView.show();
        }
      }
    });
  }

  @ReactMethod
  public void hide() {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (timePickerView == null) {
          return;
        }
        if (timePickerView.isShowing()) {
          timePickerView.dismiss();
        }
      }
    });

  }

  /**
   *  设置默认时间
   *
   */
  public void setSelectDate(Bundle args) {
    if(timePickerBuilder == null) return;
    if(args.containsKey(ARG_DATE)) {
      Calendar selectedDate = Calendar.getInstance();
      selectedDate.setTimeInMillis(args.getLong(ARG_DATE));
//          Log.d("selectedDate", String.valueOf(selectedDate.getTimeInMillis()));
      timePickerBuilder.setDate(selectedDate); // 如果不设置的话，默认是系统时间*/
    }
  }

  /**
   *  设置时间选择范围
   */
  public void setRangDate(Bundle args) {
    if(timePickerBuilder == null) return;
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    // 只设置了开始时间，没有设置结束时间
    if(args.containsKey(ARG_MINDATE) && !args.containsKey((ARG_MAXDATE))) {
      startDate.setTimeInMillis(args.getLong(ARG_MINDATE));
      endDate.set(Calendar.DATE, endDate.get(Calendar.YEAR) + 10);
      timePickerBuilder.setRangDate(startDate, endDate);
    } else if (args.containsKey(ARG_MAXDATE) && !args.containsKey(ARG_MINDATE)) {
      // 只设置了结束时间，没有设置开始时间
      startDate.setTimeInMillis(DEFAULT_MIN_DATE);
      endDate.setTimeInMillis(args.getLong(ARG_MAXDATE));
      timePickerBuilder.setRangDate(startDate, endDate);
    } else if (args.containsKey(ARG_MAXDATE) && args.containsKey(ARG_MINDATE)) {
      // 同时设置了开始时间和结束时间
      startDate.setTimeInMillis(args.getLong(ARG_MINDATE));
      endDate.setTimeInMillis(args.getLong(ARG_MAXDATE));
      timePickerBuilder.setRangDate(startDate, endDate);
    }
  }

  /**
   *  设置标题
   */
  public void setTitle(Bundle args) {
    if(timePickerBuilder == null) return;
    if(args.containsKey(ARG_TITLE)) {
      timePickerBuilder.setTitleText(ARG_TITLE); //标题文字
    }
  }

  /**
   * 设置日期选择器显示的年月日时分秒
   */
  public void setType(Bundle args) {
    if(timePickerBuilder == null) return;
    if(args.containsKey(ARG_MODE)) {
      // 设置日期时间类型
      switch (args.getString(ARG_MODE)) {
        case "year":
          timePickerBuilder.setType(new boolean[]{true, false, false, false, false, false});
          break;
        case "month":
          timePickerBuilder.setType(new boolean[]{false, true, false, false, false, false});
          break;
        case "yearMonth":
          timePickerBuilder.setType(new boolean[]{true, true, false, false, false, false});
          break;
        case "date":
          timePickerBuilder.setType(new boolean[]{true, true, true, false, false, false});
          break;
        case "dateHour":
          timePickerBuilder.setType(new boolean[]{true, true, true, true, false, false});
          break;
        case "time":
          timePickerBuilder.setType(new boolean[]{false, false, false, true, true, false});
          break;
        case "datetime":
          timePickerBuilder.setType(new boolean[]{true, true, true, true, true, false});
          break;
        default:
          timePickerBuilder.setType(new boolean[]{true, true, true, false, false, false});
      }
    }
  }

  private void commonEvent(String eventKey) {
    sendEvent(getReactApplicationContext(), PICKER_EVENT_NAME, resultMap);
  }

  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }

  // 自定义一些样式
  private void setting(ReadableMap options) {
    if(timePickerBuilder == null) return;
    // 设置标题
    if(options.hasKey(ARG_TITLE) && !options.isNull(ARG_TITLE)) {
      timePickerBuilder.setTitleText(options.getString(ARG_TITLE));
    }
    // 设置确定按钮文字
    if(options.hasKey(OK_TEXT) && !options.isNull(OK_TEXT)) {
      timePickerBuilder.setSubmitText(options.getString(OK_TEXT));
    }
    // 设置取消按钮文字
    if(options.hasKey(DISMISS_TEXT) && !options.isNull(DISMISS_TEXT)) {
      timePickerBuilder.setCancelText(options.getString(DISMISS_TEXT));
    }

    // 设置确认按钮颜色
    if(options.hasKey(CONFIRM_BTN_COLOR) && !options.isNull(CONFIRM_BTN_COLOR)) {
      ReadableArray array = options.getArray(CONFIRM_BTN_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setSubmitColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    // 设置确认按钮颜色
    if(options.hasKey(CANCEL_BTN_COLOR) && !options.isNull(CANCEL_BTN_COLOR)) {
      ReadableArray array = options.getArray(CANCEL_BTN_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setCancelColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TITLE_COLOR) && !options.isNull(TITLE_COLOR)) {
      ReadableArray array = options.getArray(TITLE_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setCancelColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(BG_COLOR) && !options.isNull(BG_COLOR)) {
      ReadableArray array = options.getArray(BG_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setBgColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TITLE_BG_COLOR) && !options.isNull(TITLE_BG_COLOR)) {
      ReadableArray array = options.getArray(TITLE_BG_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setTitleBgColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(DIVIDER_COLOR) && !options.isNull(DIVIDER_COLOR)) {
      ReadableArray array = options.getArray(DIVIDER_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setDividerColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TEXT_CENTER_COLOR) && !options.isNull(TEXT_CENTER_COLOR)) {
      ReadableArray array = options.getArray(TEXT_CENTER_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setTextColorCenter(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TEXT_OUT_COLOR) && !options.isNull(TEXT_OUT_COLOR)) {
      ReadableArray array = options.getArray(TEXT_OUT_COLOR);
      int[] colors = Utils.getColor(array);
      timePickerBuilder.setTextColorOut(argb(colors[3], colors[0], colors[1], colors[2]));
    }
  }

  private Bundle createFragmentArguments(ReadableMap options) {
    final Bundle args = new Bundle();
    if (options.hasKey(ARG_DATE) && !options.isNull(ARG_DATE)) {
      args.putLong(ARG_DATE, (long) options.getDouble(ARG_DATE));
    }
    if (options.hasKey(ARG_MINDATE) && !options.isNull(ARG_MINDATE)) {
      args.putLong(ARG_MINDATE, (long) options.getDouble(ARG_MINDATE));
    }
    if (options.hasKey(ARG_MAXDATE) && !options.isNull(ARG_MAXDATE)) {
      args.putLong(ARG_MAXDATE, (long) options.getDouble(ARG_MAXDATE));
    }
    if (options.hasKey(ARG_MODE) && !options.isNull(ARG_MODE)) {
      args.putString(ARG_MODE, options.getString(ARG_MODE));
    }
    return args;
  }

}
