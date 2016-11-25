/**
 * Touch
 * Created by panchenhuan on 16/10/21.
 */
'use strict';
import React, {Component} from 'react';
import {
    View,
    Text, ToastAndroid, NativeModules, StyleSheet, NativeAppEventEmitter, Animated, PanResponder
} from 'react-native';

var touchModule = NativeModules.TouchIdModule;
var SnackbarModule = NativeModules.SnackbarModule;

var Icon = require('react-native-vector-icons/Entypo');
export default class Touch extends Component {
    componentWillMount() {
        NativeAppEventEmitter.addListener("touchIDOnSuccess", ()=> {
            ToastAndroid.show('touchIDOnSuccess', ToastAndroid.SHORT);
        });
        NativeAppEventEmitter.addListener("touchIDOnError", (error)=> {
            if (null != error && error.errId != 5) {
                ToastAndroid.show('touchIDOnError' + error.errString, ToastAndroid.SHORT);
            }
        });
    }

    //noinspection JSAnnotator
    state: {
        fadeAnim:Animated,
        currentAlpha:number,
        trans:AnimatedValueXY,
    };
    //noinspection JSAnnotator
    _panResponder: PanResponder;

    constructor(props) {
        super(props);
        this.state = {//设置初值
            currentAlpha: 1.0,//标志位，记录当前value
            fadeAnim: new Animated.Value(1.0),
            trans: new Animated.ValueXY(),
        };
        this._panResponder = PanResponder.create({
            onStartShouldSetPanResponder: () => true, //响应手势
            onPanResponderMove: Animated.event(
                [null, {dx: this.state.trans.x, dy: this.state.trans.y}] // 绑定动画值
            ),
            onPanResponderRelease: ()=> {//手松开，回到原始位置
                Animated.spring(this.state.trans, {toValue: {x: 0, y: 0}}
                ).start();
            },
            onPanResponderTerminate: ()=> {//手势中断，回到原始位置
                Animated.spring(this.state.trans, {toValue: {x: 0, y: 0}}
                ).start();
            },
        });
    }


    // 渲染
    render() {
        return (
            <View style={styles.container}>
                <Text style={styles.welcome} onPress={()=> {
                    touchModule.touchIDStart();
                }}>
                    Touch to check.
                </Text>
                <Text style={styles.welcome} onPress={()=> {
                    SnackbarModule.show('Snackbar Message','Action',()=>{
                        ToastAndroid.show('Click Action',ToastAndroid.SHORT);
                    });
                }}>
                    Show Snackbar.
                </Text>
                <Animated.View style={{
                    width: 50,
                    height: 50,
                    borderRadius: 50,
                    backgroundColor: '#213970',
                    justifyContent: 'center',
                    alignItems: 'center',
                    transform: [
                        {translateY: this.state.trans.y},
                        {translateX: this.state.trans.x},
                    ],
                }}
                               {...this._panResponder.panHandlers}
                >
                    <Icon
                        name="aircraft"   //图片名连接,可以到这个网址搜索:http://ionicons.com/, 使用时:去掉前面的 "icon-" !!!!
                        size={30}   //图片大小
                        color="red"  //图片颜色
                    />
                </Animated.View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#CDA710',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});