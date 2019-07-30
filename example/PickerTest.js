/**
 * Bootstrap of PickerTest
 */

import React, {Component} from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    Dimensions,
    StyleSheet
} from 'react-native';
import dayjs from 'dayjs';
import DatePickerView from './DatePickerView';
import area from './area.json';

export default class PickerTest extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            planstarttime: new Date(),
        }
    }

    onChangeDate = (date) => {
        this.setState({
          planstarttime: date,
        })
      }

    render() {
        const { planstarttime } = this.state;
        return (
            <View style={{height: Dimensions.get('window').height}}>
                <DatePickerView
                //   value={planstarttime}
                // minDate={dayjs('2000-01-01').toDate()}
                //   maxDate={dayjs().add(1, 'day').toDate()}
                confirmBtnColor={[ 255, 136, 8, 1 ]}
                cancelBtnColor={[ 255, 136, 8, 1 ]}
                onChange={this.onChangeDate}
                >
                <TouchableOpacity style={styles.datePicker}>
                    {
                    planstarttime
                        ?
                        (
                        <Text style={styles.dateText}>{dayjs(planstarttime).format('YYYY-MM-DD')}</Text>
                        )
                        : (
                        <Text style={styles.placeholder}>默认选择日期</Text>
                        )
                    }
                </TouchableOpacity>
                </DatePickerView>
                <DatePickerView
                value={planstarttime}
                minDate={dayjs('2000-01-01').toDate()}
                maxDate={dayjs().add(1, 'day').toDate()}
                confirmBtnColor={[ 255, 136, 8, 1 ]}
                cancelBtnColor={[ 255, 136, 8, 1 ]}
                onChange={this.onChangeDate}
                >
                <TouchableOpacity style={styles.datePicker}>
                    {
                    planstarttime
                        ?
                        (
                        <Text style={styles.dateText}>{dayjs(planstarttime).format('YYYY-MM-DD')}</Text>
                        )
                        : (
                        <Text style={styles.placeholder}>默认选择日期</Text>
                        )
                    }
                </TouchableOpacity>
                </DatePickerView>
            </View>
        );
    }
};

const styles = StyleSheet.create({
    container: {
      flex: 1,
      marginTop: 10,
      backgroundColor: '#fafafa',
      padding: 16,
    },
    content: {
      flex: 1,
    },
    item: {
  
    },
    itemrow: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: "flex-start",
    },
    label: {
      fontSize: 16,
      marginBottom: 15,
      color: "#666666",
    },
    datePicker: {
      flexDirection: 'row',
      width: 140,
      height: 36,
      borderRadius: 4,
      backgroundColor: "#ffffff",
      borderStyle: "solid",
      borderWidth: StyleSheet.hairlineWidth,
      borderColor: "#e1e1e1",
      justifyContent: 'center',
      padding: 10,
      marginBottom: 25,
    },
    dateText: {
      flex: 1,
    },
    placeholder: {
      flex: 1,
      color: "#c8c8c8",
    },
    radioGroup: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      marginRight: -15,
    },
    radio: {
      justifyContent: 'center',
      alignItems: 'center',
      height: 36,
      width: 99,
      borderRadius: 4,
      backgroundColor: "#ffffff",
      borderStyle: "solid",
      borderWidth: StyleSheet.hairlineWidth,
      borderColor: "#b9b9b9",
      marginRight: 15,
      marginBottom: 15,
    },
    radioText: {
      color: "#666666",
    },
    activeRadio: {
      borderColor: "#ff8808",
    },
    activeRadioText: {
      color: "#ff8808",
    },
    footer: {
      
      flexDirection: 'row',
      marginBottom: 50,
      marginHorizontal: -8,
    },
    button: {
      flex: 1,
      marginHorizontal: 8,
    },
    restButton: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      marginHorizontal: 8,
      width: 168,
      height: 44,
      borderRadius: 4,
      borderWidth: StyleSheet.hairlineWidth,
      borderColor: "#bbbbbb",
    },
    restButtonText: {
      fontSize: 16,
        color: "#666666",
    },
  })