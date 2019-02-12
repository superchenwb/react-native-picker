package com.zy.pickerview.bean;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.ArrayList;
import java.util.List;

public class OptionBean implements IPickerViewData {
  private String value = "";
  private String label = "";

  private List<OptionBean> children = new ArrayList<>();

  public OptionBean(String value, String label) {
    this.value = value;
    this.label = label;
  }

  public OptionBean(String value, String label, List<OptionBean> children) {
    this.value = value;
    this.label = label;
    this.children = children;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public List<OptionBean> getChildren() {
    return children;
  }

  public void setChildren(List<OptionBean> children) {
    this.children = children;
  }

  @Override
  public String getPickerViewText() {
    return label;
  }
}
