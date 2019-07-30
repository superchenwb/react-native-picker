import React, { PureComponent } from 'react'
import { View, Text, TouchableHighlight } from 'react-native'
import PropTypes from 'prop-types'
import dayjs from 'dayjs';
// import { List } from '@ant-design/react-native'
import { DatePicker } from 'react-native-zy-picker'
// import { formatDate } from '../../utils'

// const ListItem = List.Item

export default class DatePickerView extends PureComponent {

  static propTypes = {
    mode: PropTypes.string,
  }

  static defaultProps = {
    mode: 'date',
  }

  componentDidMount() {
    
  }

  onPress = () => {
    const { onChange, value, minDate, maxDate, format, mode, confirmBtnColor, cancelBtnColor, onPress } = this.props
    console.log('value', dayjs(value).format('yyyy-MM-dd'))
    try {
      DatePicker.open({
        mode,
        date: value,
        minDate,
        maxDate,
        format,
        confirmBtnColor,
        cancelBtnColor,
        callback: (date) => {
          if(onChange) {
            // alert(date)
            let time = date.replace(/-/g,':').replace(' ',':');
            // console.log('time', time)
            time = time.split(':');
            // console.log('time2', time)
            const dateTime = new Date(time[0],(time[1]-1),time[2],time[3],time[4],time[5]);
            console.log('dateTime', dateTime)

            onChange(dateTime)
          }
        },
      });
      DatePicker.show()
    } catch ({code, message}) {
      console.warn('Cannot open date picker', message);
    }
    if(onPress) {
      onPress();
    }
  }

  render() {
    const { title, value, mode, children, extra } = this.props
    if(children && React.isValidElement(children)) {
      return React.cloneElement(children, {
        onPress: this.onPress,
        extra: value || (extra || '请选择'),
      })
    }

    console.log('-----value', value)
    
    return (
      <TouchableHighlight
        onPress={this.onPress} 
        arrow 
        extra={(<View style={{ flex: 1.5, flexDirection: 'column' }}><Text numberOfLines={1} style={{ textAlign: 'right', fontSize: 17 }}>{extra}</Text></View>)}
      >
        {title}
      </TouchableHighlight>
    )
  }
}