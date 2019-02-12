
import {
    NativeModules,
    NativeAppEventEmitter,
  } from 'react-native'
  
  const ZYPickerModule = NativeModules.ZYPicker;
  
  // 根据value值计算出对应的数组下标以及label
  const getIndexArr = ({ data, cascade, value }) => {
    const selectValue = [];
    const selectLabel = [];
    // 联级
    if(cascade && value && value.length > 0) {
      data.forEach((options, i) => {
        if(options.value === value[0]) {
          selectValue.push(i);
          selectLabel.push(options.label);
          if(options.children && options.children.length > 0) {
            options.children.forEach((option2s, j) => {
              if(option2s.value === value[1]) {
                selectValue.push(j);
                selectLabel.push(option2s.label);
                if(option2s.children && option2s.children.length > 0) {
                  option2s.children.forEach((option3s, k) => {
                    if(option3s.value === value[2]) {
                      selectValue.push(k);
                      selectLabel.push(option3s.label);
                    }
                  });
                }
              }
            });
          }
        }
      });
    } else if(value && value.length > 0) {
      data.forEach((options, i) => {
        if(options instanceof Array) {
          console.log('-------options instanceof Array-----')
          options.forEach((option, j) => {
            value.forEach(v => {
              if(option.value === v) {
                selectValue.push(j)
                selectLabel.push(option.label);
              }
            })
          })
        } else if (options.value === value[0]) {
          console.log('-------options.value-----', options.value, selectValue)
          selectValue.push(i)
          selectLabel.push(options.label);
        }
      })
    }
    return { selectValue, selectLabel };
  }
  
  class Picker {
  
    static open(options) {
      const { selectValue } = getIndexArr(options);
      ZYPickerModule.open({...options, selectValue });
  
      if(this.listener) {
        this.listener.remove();
      }
      this.listener = NativeAppEventEmitter.addListener('pickerEvent', event => {
        options.callback(event.selectedValue);
      });
    }
  
    static defualtSelectLabel(options) {
      const { selectLabel } = getIndexArr(options);
      return selectLabel;
    }
  
    static show() {
      ZYPickerModule.show();
    }
  
    static setPickerData(options) {
      ZYPickerModule.setPickerData(options);
    }
  
  }
  
  module.exports = Picker;
  