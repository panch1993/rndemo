/**
 * BaiduMapView
 * Created by panchenhuan on 16/10/13.
 */
'use strict';
import React, {Component, PropTypes} from 'react';
import {
    requireNativeComponent, View, Dimensions, StyleSheet, Image, TextInput, ToastAndroid, TouchableOpacity,
    UIManager, findNodeHandle,
} from 'react-native';
import TextField from 'react-native-md-textinput';
import Icon from 'react-native-vector-icons/EvilIcons';

let BdMapView = requireNativeComponent('BaiduMapView', BaiduMapView);

var width = Dimensions.get('window').width;
var height = Dimensions.get('window').height;
var BD_REF = 'bd_ref';
var TX_IN = 'tx_in';
class BaiduMapView extends Component {

    static propTypes = {
        ...View.propTypes,// 包含默认的View的属性
        //定位模式 0 普通，1 跟随， 2 罗盘
        userTrackingMode: PropTypes.number,
        //周边搜索 keyword
        keyword: PropTypes.string,
        //是否显示定位图层，默认显示
        showUserLocation: PropTypes.bool,
        //缩放级别。
        zoomLevel: PropTypes.number,
    };

    _search() {
        UIManager.BaiduMapView.dispatchViewManagerCommand(
            findNodeHandle(this.refs[BD_REF]),
            2,//Commands.pause与native层定义的COMMAND_PAUSE_NAME一致
            null//命令携带的参数数据
        );
    }

    render() {
        return (
            <View style={{flex: 1}}>
                <Image style={styles.backgroundImg}
                       resizeMethod={'scale'}
                       source={{uri: 'http://pic.90sjimg.com/back_pic/u/00/28/77/06/55fb684bca08a.jpg'}}>
                    <TextField
                        wrapperStyle={styles.search}
                        inputStyle={styles.textInputs}
                        label={'Keyword'}
                        labelColor={"#ffffff"}
                        highlightColor={'#CDA710'}
                        textColor={"#CDA710"}
                        ref={TX_IN}
                        />
                    <TouchableOpacity style={{margin:5}} >
                        <Icon.Button
                            style={{height: 30, width: width * 0.26}}
                            name="search" backgroundColor="#18332F"
                            onPress={()=> {
                                UIManager.dispatchViewManagerCommand(
                                    findNodeHandle(this.refs[BD_REF]),
                                    2,//Commands.pause与native层定义的COMMAND_PAUSE_NAME一致
                                    [this.refs[TX_IN].state.text]//命令携带的参数数据
                                );
                            }}>
                            Search
                        </Icon.Button>
                    </TouchableOpacity>
                    <TouchableOpacity style={{margin:5}} >
                        <Icon.Button
                            style={{height: 30, width: width * 0.28}}
                            name="location" backgroundColor="#18332F"
                            onPress={()=> {
                                UIManager.dispatchViewManagerCommand(
                                    findNodeHandle(this.refs[BD_REF]),
                                    3,//Commands.pause与native层定义的COMMAND_PAUSE_NAME一致
                                    null//命令携带的参数数据
                                );
                            }}>
                            Location
                        </Icon.Button>
                    </TouchableOpacity>
                </Image>
                <BdMapView {...this.props}
                           style={{width: width, height: height * 0.85}}
                           showUserLocation={true}
                           zoomLevel={18.0}
                           locationMode={0}
                           ref={BD_REF}/>
            </View>
        )
    }
}
const styles = StyleSheet.create({
    backgroundImg: {
        width: width,
        height: height * 0.15,
        // flexDirection:'row',
        alignItems: 'flex-end',
        justifyContent: 'space-between',
        flexDirection: 'row',
        paddingLeft:5
    },
    search: {
        width: width * 0.35,
    },
    textInputs: {
        fontSize: 12,
        height: 34,
        lineHeight: 34
    },
});
module.exports = BaiduMapView;

