import {
  NativeModules,
  NativeAppEventEmitter,
} from 'react-native'
  
const DatePickerModule = NativeModules.ZYDateTimePicker;
  
  /**
   * Convert a Date to a timestamp.
   */
  function _toMillis(options, key) {
    const dateVal = options[key];
    // Is it a Date object?
    if (dateVal && typeof dateVal === 'object' && typeof dateVal.getMonth === 'function') {
      options[key] = dateVal.getTime();
    }
  }
  
  class DatePicker {
    static open(options) {
      const optionsMs = options;
      if (optionsMs) {
        _toMillis(options, 'date');
        _toMillis(options, 'minDate');
        _toMillis(options, 'maxDate');
      }
      DatePickerModule.open(options)
      if(this.listener) {
        this.listener.remove();
      }
      this.listener = NativeAppEventEmitter.addListener('timePickerEvent', event => {
        options.callback(event.date, event.year, event.month, event.day);
      });
    }
  
    static show() {
      return DatePickerModule.show();
    }
  }
  
  module.exports = DatePicker;
  