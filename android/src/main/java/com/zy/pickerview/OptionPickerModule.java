package com.zy.pickerview;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.zy.pickerview.bean.OptionBean;
import com.zy.pickerview.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.graphics.Color.argb;

public class OptionPickerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  private static final String REACT_CLASS = "ZYPicker";
  // 数据
  private static final String PICKER_DATA = "data";
  private static final String SELECTED_VALUE = "value";
  // 标题
  private static final String ARG_TITLE = "title";
  // 是否联动
  private static final String CASCADE = "cascade";

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

  private static final String SELECT_VALUE = "selectValue";

  private static final String PICKER_EVENT_NAME = "pickerEvent";

  private static final String EVENT_KEY_CONFIRM = "confirm";

  private OptionsPickerBuilder optionsPickerBuilder;
  private OptionsPickerView pickerView;

  private List<List<OptionBean>> optionList;

  private WritableMap resultMap;

  private ReadableMap options = Arguments.createMap();

  List<OptionBean> options1Items = new ArrayList<>();
  List<List<OptionBean>> options2Items = new ArrayList<>();
  List<List<List<OptionBean>>> options3Items = new ArrayList<>();
  // 联级层级
  private int cascadeTotal = 1;

//  private List<OptionBean> optionBeanList;

  private class PickerListener implements OnOptionsSelectListener {
    private final ReadableMap mOptions;

    public PickerListener( final ReadableMap options) {
      mOptions = options;
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3 ,View v) {
      if (getReactApplicationContext().hasActiveCatalystInstance()) {
        WritableArray resultValue = Arguments.createArray();
        resultMap = Arguments.createMap();
        // 返回的分别是三个级别的选中位置
        int[] options = {options1, options2, options3};
        // 判断是否是联级结构
        boolean cascade = mOptions.hasKey(CASCADE) && mOptions.getBoolean(CASCADE);
        if(cascade) {
          options1Items.get(options1).getValue();
          if(cascadeTotal == 3) {
            resultValue.pushString(options1Items.get(options1).getValue());
            resultValue.pushString(options2Items.get(options1).get(options2).getValue());
            resultValue.pushString(options3Items.get(options1).get(options2).get(options3).getValue());
          } else if(cascadeTotal == 2) {
            resultValue.pushString(options1Items.get(options1).getValue());
            resultValue.pushString(options2Items.get(options1).get(options2).getValue());
          } else if(cascadeTotal == 1) {
            resultValue.pushString(options1Items.get(options1).getValue());
          }

        } else {
          for(int i = 0; i < optionList.size(); i++) {
            List<OptionBean> optionBeans = optionList.get(i);
            resultValue.pushString(optionBeans.get(options[i]).getValue());
          }
        }
        resultMap.putArray("selectedValue", resultValue);
        commonEvent(EVENT_KEY_CONFIRM);
      }

    }
  }

  public OptionPickerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addLifecycleEventListener(this);
  }

  private void build() {
    optionList = new ArrayList<>();
    options1Items = new ArrayList<>();
    options2Items = new ArrayList<>();
    options3Items = new ArrayList<>();
    final Activity activity = getCurrentActivity();

    ReadableArray pickerData = Arguments.createArray();
    if(options.hasKey(PICKER_DATA)) {
      pickerData = options.getArray(PICKER_DATA);
    }
    //时间选择器
    final PickerListener listener = new PickerListener(options);
    //条件选择器
    optionsPickerBuilder = new OptionsPickerBuilder(activity, listener);

    setting(options);
    pickerView = optionsPickerBuilder.build();
    if(options.hasKey(SELECT_VALUE) && !options.isNull(SELECT_VALUE)) {
      ReadableArray selectValueArray = options.getArray(SELECT_VALUE);
      setSelectedValue(selectValueArray);
    }
    
    setPickerData(pickerData);
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
        if (pickerView == null) {
          build();
        }
        if (!pickerView.isShowing()) {
          pickerView.show();
        }
      }
    });
  }

  @ReactMethod
  public void hide() {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (pickerView == null) {
          return;
        }
        if (pickerView.isShowing()) {
          pickerView.dismiss();
        }
      }
    });
  }

  /**
   *  传入数据的结构
   * @param pickerData
   */
  @ReactMethod
  public void setPickerData(@Nullable ReadableArray pickerData) {
    if(pickerView == null) return;
    // 置空，防止数据重复
    optionList = new ArrayList<>();
    options1Items = new ArrayList<>();
    options2Items = new ArrayList<>();
    options3Items = new ArrayList<>();
    // 判断是否是联级结构
    boolean cascade = options.hasKey(CASCADE) && options.getBoolean(CASCADE);
    if(cascade && pickerData.size() > 0 && "Map".equals(pickerData.getType(0).name())) {
      setCascadePickerData(pickerData);
      if(cascadeTotal == 3) {
        pickerView.setPicker(options1Items, options2Items, options3Items);
      } else if(cascadeTotal == 2) {
        pickerView.setPicker(options1Items, options2Items, null);
      } else if(options1Items.size() > 0) {
        pickerView.setPicker(options1Items);
      }
    } else if(pickerData.size() > 0) {
      setSimplePickerData(pickerData);
      if(optionList.size() == 1) {
        pickerView.setNPicker(optionList.get(0), null, null);
      } else if(optionList.size() == 2) {
        pickerView.setNPicker(optionList.get(0), optionList.get(1), null);
      } else if(optionList.size() == 3) {
        pickerView.setNPicker(optionList.get(0), optionList.get(1), optionList.get(2));
      }
    }
  }

  /**
   * 设置选择器初始值
   * @param selectValueArray
   */
  @ReactMethod
  public void setSelectedValue(ReadableArray selectValueArray) {
    if(pickerView == null) return;

    if(selectValueArray.size() == 3) {
      pickerView.setSelectOptions(selectValueArray.getInt(0), selectValueArray.getInt(1), selectValueArray.getInt(2));
    } else if(selectValueArray.size() == 2) {
      pickerView.setSelectOptions(selectValueArray.getInt(0), selectValueArray.getInt(1));
    } else if(selectValueArray.size() == 1) {
      pickerView.setSelectOptions(selectValueArray.getInt(0));
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



  /**
   *  传入数据的结构

   *  5. 联级结构，一级结构（与非联级结构一致）
   *  [{ value: '1', label: '1' }]
   *  6. 联级结构，二级结构
   *  [{ value: '1', label: '1', children: [ {value: '1-1', label: '1-1'} ] }]
   *  7. 联级结构，三级结构
   *  [{ value: '1', label: '1', children: [ {value: '1-1', label: '1-1', children: [{ value: '1-1-1', label: '1-1-1' }] } ] }]
   * @param readableArray
   */
  private void setCascadePickerData(ReadableArray readableArray) {
    for(int i = 0; i< readableArray.size(); i++) {
      ReadableMap readableMap = readableArray.getMap(i);
      String value = readableMap.getString("value");
      String label = readableMap.getString("label");
      OptionBean optionBean = new OptionBean(value, label);
      options1Items.add(optionBean);
      ArrayList<OptionBean> options2List = new ArrayList<>();
      options2Items.add(options2List);
      List<List<OptionBean>> options3List = new ArrayList<>();
      options3Items.add(options3List);

      // 判断是否有children属性
      if(readableMap.hasKey("children") && readableMap.getArray("children").size() > 0) {
        cascadeTotal = 2;
        ReadableArray options2Array = readableMap.getArray("children");
        for (int j = 0; j < options2Array.size(); j++) {
          ReadableMap options2Map = options2Array.getMap(j);
          String options2Value = options2Map.getString("value");
          String options2Label = options2Map.getString("label");
          OptionBean options2Bean = new OptionBean(options2Value, options2Label);
          options2List.add(options2Bean);
          List<OptionBean> options3Beans = new ArrayList<>();
          options3List.add(options3Beans);
          if(options2Map.hasKey("children") && options2Map.getArray("children").size() > 0) {
            cascadeTotal = 3;
            ReadableArray readableArray3 = options2Map.getArray("children");
            for (int k = 0; k < readableArray3.size(); k++) {
              ReadableMap options3Map = readableArray3.getMap(k);
              String options3Value = options3Map.getString("value");
              String options3Label = options3Map.getString("label");
              OptionBean childrenBean3 = new OptionBean(options3Value, options3Label);
              options3Beans.add(childrenBean3);
            }
          } else {
//            cascadeTotal = 3;
            OptionBean emptyOption = new OptionBean("", "");
            options3Beans.add(emptyOption);
          }
        }
      } else {
        // 如果没有第二级，设置空数据
//        cascadeTotal = 2;
        OptionBean emptyOption = new OptionBean("", "");
        options2List.add(emptyOption);
      }
    }
  }

  /**
   *  传入数据的结构
   *  1. 非联级结构, 一级结构
   *  [{ value: '1', label: '1', }, { value: '2', label: '', }]
   *  2. 非联级结构，一级结构
   *  [[{value: '', label: ''}, {value: '', label: ''}, {value: '', label: ''}]]
   *  3. 非联级结构，二级结构
   *  [ [{ value: '1', label: '1', }], [{ value: '2', label: '2', }]]
   *  4.非联级结构，三级结构
   *  [ [{ value: '1', label: '1', }], [{ value: '2', label: '2', }], [{ value: '3', label: '3', }]]
   * @param readableArray
   */
  private void setSimplePickerData(ReadableArray readableArray) {
    for(int i = 0; i < readableArray.size(); i++) {
      String name = readableArray.getType(i).name();
      if("Array".equals(name)) {
        List<OptionBean> optionBeanList = new ArrayList<>();
        ReadableArray optionArray = readableArray.getArray(i);
        for (int j = 0; j < optionArray.size(); j++) {
          ReadableMap childMap = optionArray.getMap(j);
          String value = childMap.getString("value");
          String label = childMap.getString("label");
          OptionBean bean = new OptionBean(value, label);
          optionBeanList.add(bean);
        }
        optionList.add(optionBeanList);
      } else if("Map".equals(name)) {
        ReadableMap readableMap = readableArray.getMap(i);
        String value = readableMap.getString("value");
        String label = readableMap.getString("label");
        OptionBean optionBean = new OptionBean(value, label);
        options1Items.add(optionBean);
      }
    }
    // 判断是否是联级结构
    boolean cascade = options.hasKey(CASCADE) && options.getBoolean(CASCADE);
    if(!cascade && optionList.size() == 0) {
      optionList.add(options1Items);
    }
  }

  // 自定义一些样式
  private void setting(ReadableMap options) {

    // 设置标题
    if(options.hasKey(ARG_TITLE) && !options.isNull(ARG_TITLE)) {
      optionsPickerBuilder.setTitleText(options.getString(ARG_TITLE));
    }
    // 设置确定按钮文字
    if(options.hasKey(OK_TEXT) && !options.isNull(OK_TEXT)) {
      optionsPickerBuilder.setSubmitText(options.getString(OK_TEXT));
    }
    // 设置取消按钮文字
    if(options.hasKey(DISMISS_TEXT) && !options.isNull(DISMISS_TEXT)) {
      optionsPickerBuilder.setCancelText(options.getString(DISMISS_TEXT));
    }

    // 设置确认按钮颜色
    if(options.hasKey(CONFIRM_BTN_COLOR) && !options.isNull(CONFIRM_BTN_COLOR)) {
      ReadableArray array = options.getArray(CONFIRM_BTN_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setSubmitColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    // 设置确认按钮颜色
    if(options.hasKey(CANCEL_BTN_COLOR) && !options.isNull(CANCEL_BTN_COLOR)) {
      ReadableArray array = options.getArray(CANCEL_BTN_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setCancelColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TITLE_COLOR) && !options.isNull(TITLE_COLOR)) {
      ReadableArray array = options.getArray(TITLE_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setCancelColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(BG_COLOR) && !options.isNull(BG_COLOR)) {
      ReadableArray array = options.getArray(BG_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setBgColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TITLE_BG_COLOR) && !options.isNull(TITLE_BG_COLOR)) {
      ReadableArray array = options.getArray(TITLE_BG_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setTitleBgColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(DIVIDER_COLOR) && !options.isNull(DIVIDER_COLOR)) {
      ReadableArray array = options.getArray(DIVIDER_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setDividerColor(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TEXT_CENTER_COLOR) && !options.isNull(TEXT_CENTER_COLOR)) {
      ReadableArray array = options.getArray(TEXT_CENTER_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setTextColorCenter(argb(colors[3], colors[0], colors[1], colors[2]));
    }

    if(options.hasKey(TEXT_OUT_COLOR) && !options.isNull(TEXT_OUT_COLOR)) {
      ReadableArray array = options.getArray(TEXT_OUT_COLOR);
      int[] colors = Utils.getColor(array);
      optionsPickerBuilder.setTextColorOut(argb(colors[3], colors[0], colors[1], colors[2]));
    }
  }

  @Override
  public void onHostResume() {

  }

  @Override
  public void onHostPause() {
    hide();
    pickerView = null;
  }

  @Override
  public void onHostDestroy() {
    hide();
    pickerView = null;
  }

  @Override
  public String getName() {
    return REACT_CLASS;
  }
}
