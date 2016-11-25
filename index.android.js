/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';
import {
    AppRegistry,
    StyleSheet, Dimensions,
    Text, DrawerLayoutAndroid,
    View, ListView, TouchableOpacity, ToastAndroid, StatusBar, Platform, Image, Navigator, BackAndroid
} from 'react-native';

import SpeechView from './app/SpeechView'
import ModalExample from './app/ModalExample'
import HomePage from './app/HomePage'
import BaiduMap from './app/BaiduMap'
import Read from './app/Read'
import Watermark from './app/Watermark'
import Touch from './app/Touch'
export const ABOVE_LOLIPOP = Platform.Version && Platform.Version > 19;

var width = Dimensions.get('window').width;
var height = Dimensions.get('window').height;
var currentID = '我的主页';
var FontAwesome = require('react-native-vector-icons/FontAwesome');
var Entypo = require('react-native-vector-icons/Entypo');
var iconNames = [<FontAwesome style={{margin: 10}} name="home" size={20} color="#999"/>,
    <FontAwesome style={{margin: 10}} name="volume-up" size={20} color="#999"/>,
    <FontAwesome style={{margin: 10}} name="map" size={20} color="#999"/>,
    <Entypo style={{margin: 10}} name="hand" size={20} color="#999"/>,
    <FontAwesome style={{margin: 10}} name="print" size={20} color="#999"/>,
    <FontAwesome style={{margin: 10}} name="book" size={20} color="#999"/>,
    <FontAwesome style={{margin: 10}} name="th" size={20} color="#999"/>];
var time = 0;
class rnDemo extends Component {

    setCurrentView(rowData) {
        if (currentID !== rowData) {
            if (rowData === '我的主页') {
                this.setState({
                    view: (<HomePage openDrawer={this.open.bind(this)}/>)
                })
            } else if (rowData === '语音识别') {
                this.setState({
                    view: (<SpeechView openDrawer={this.open.bind(this)}/>)
                })
            } else if (rowData === '百度地图') {
                this.setState({
                    view: (<BaiduMap openDrawer={this.open.bind(this)}/>)
                })
            } else if (rowData === '指纹识别') {
                this.setState({
                    view: (<Touch openDrawer={this.open.bind(this)}/>)
                })
            } else if (rowData === '图文水印') {
                this.setState({
                    view: (<Watermark openDrawer={this.open.bind(this)}/>)
                })
            } else if (rowData === '每日一文') {
                this.setState({
                    view: (<Read openDrawer={this.open.bind(this)}/>)
                })
            } else {
                this.setState({
                    view: (<ModalExample/>)
                })

            }
            currentID = rowData;
        }
    }

    //组件挂载的时候调用
    componentDidMount() {
        BackAndroid.addEventListener('hardwareBackPress', function () {
            // ToastAndroid.show("再次点击退出", ToastAndroid.SHORT);
            //获取今天日期
            if (time == 0) {
                time = new Date().valueOf();
                ToastAndroid.show("再次点击退出", ToastAndroid.SHORT);
                return true;
            } else {
                let time2 = new Date().valueOf();
                if (time2 - time > 2000) {
                    ToastAndroid.show("再次点击退出", ToastAndroid.SHORT);
                    time = time2;
                    return true;
                }

            }
            return false;

        });
    }

    constructor(props) {
        super(props);
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            dataSource: ds.cloneWithRows([
                '我的主页', '语音识别', '百度地图', '指纹识别', '图文水印', '每日一文'
            ]),
            view: (<HomePage openDrawer={this.open.bind(this)}/>),
        };
    }

    _renderRow(rowData, xxx, rowID) {
        // var ImgR = ImagRes[rowID];
        return (<TouchableOpacity style={{flexDirection: 'row', alignItems: 'center'}}
                                  onPress={()=> {
                                      //ToastAndroid.show(rowID, ToastAndroid.SHORT);
                                      this.drawer.closeDrawer();
                                      this.setCurrentView(rowData);
                                  }}>
            {iconNames[rowID]}
            <Text style={styles.lvItemStyle}>{rowData}</Text>
        </TouchableOpacity>);
    }

    open() {
        this.drawer.openDrawer();
    }

    render() {
        return (
            <View style={{flex: 1}}>
                <StatusBar
                    barStyle='light-content'
                    backgroundColor='transparent'
                    style={{height: 25}}
                    translucent={ABOVE_LOLIPOP}
                />
                <DrawerLayoutAndroid
                    style={{paddingTop: 25}}
                    drawerWidth={width * 0.65}
                    ref={(drawer) => this.drawer = drawer }
                    renderNavigationView={() =>
                        <View style={styles.lvStyle}>
                            <Image style={{width: width * 0.65, height: 180, justifyContent: 'flex-end'}}
                                   source={require('./app/img/bg.png')}>
                                <Text style={styles.textTitle}>Menu</Text>
                                <Text style={styles.textSmall}>For this rnDemo</Text>
                            </Image>
                            <ListView
                                style={{marginTop: 5}}
                                dataSource={this.state.dataSource}
                                renderRow={this._renderRow.bind(this)}/>
                        </View>}

                >
                    {this.state.view}
                </DrawerLayoutAndroid>
            </View>
        );
    }
}

const
    styles = StyleSheet.create({
        lvStyle: {
            backgroundColor: '#fff', flex: 1
        },
        lvItemStyle: {
            fontSize: 18,
            color: '#999',
            textAlign: 'center',
            marginLeft: 10
        },
        textTitle: {
            color: '#fff',
            fontSize: 30,
            marginLeft: 10
        },
        textSmall: {
            color: '#fff',
            fontSize: 10,
            marginLeft: 12,
            marginBottom: 10
        }
    });
AppRegistry
    .registerComponent(
        'rnDemo'
        , () =>
            rnDemo
    )
;
