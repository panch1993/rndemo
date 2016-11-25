/**
 * speechView
 * Created by panchenhuan on 16/10/10.
 */
'use strict';
import React, {Component} from 'react';
import {
    View,Alert,ToastAndroid,StyleSheet,
    Text,TouchableOpacity,NativeModules, NativeAppEventEmitter
} from 'react-native';

var iFlyListen = NativeModules.IFlyListenModule;

/**
 * 语音输入界面
 */
export default class SpeechView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            textBody: ' '
        };

    }

    componentWillMount() {
        console.log(NativeModules);
        NativeAppEventEmitter.addListener("onBeginOfSpeech",()=>{
           console.log('onBeginOfSpeech',arguments);
        });
        NativeAppEventEmitter.addListener("onError",(error)=>{
           console.log("onError",error);
        });
        NativeAppEventEmitter.addListener("onEndOfSpeech",()=>{
           console.log("onEndOfSpeech",arguments);
        });
        NativeAppEventEmitter.addListener("onResult",(result)=>{
           console.log("onResult",result);
            this.setState({
                textBody:result.results
            })
        });
        NativeAppEventEmitter.addListener("onVolumeChanged",()=>{
           console.log("onVolumeChanged",arguments);
        });
    }
    startListening() {
        iFlyListen.startListening();
        // recode.startRecording();
    }

    stopListening() {
        iFlyListen.stopListening();
        // recode.stopRecording("test");
    }

    cancel() {
        iFlyListen.cancel((str)=> {
            ToastAndroid.show(str, ToastAndroid.SHORT);
        });
        // recode.pauseRecording();
    }


    // 渲染
    render() {
        return (
            <View style={styles.container}>
                <View style={{flex: 1,paddingTop:25}}>
                    <Text style={styles.title}>讯飞语音输入</Text>
                    <Text style={styles.content}>{this.state.textBody}</Text>
                    <View style={{backgroundColor: '#fff', height: 1, marginLeft: 10, marginRight: 10}}/>
                </View>
                <View
                    style={{height: 100, flexDirection: 'row', marginLeft: 10, marginRight: 10, alignItems: 'center'}}>
                    <TouchableOpacity style={styles.button} onPress={() => {
                        this.startListening()
                    }}>
                        <View><Text style={{color: 'white'}}>开始</Text></View>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.button} onPress={() => {
                        this.stopListening()

                    }}>
                        <View><Text style={{color: 'white'}}>停止</Text></View>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.button} onPress={() => {
                        this.cancel()
                    }}>
                        <View><Text style={{color: 'white'}}>取消</Text></View>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#2f3031', flexDirection: 'column'
    },
    title: {
        height: 50,
        color: '#fff',
        fontSize: 20,
        paddingTop: 10, textAlign: 'center'
    },
    content: {
        borderWidth: 0.3, borderColor: 'white', color: '#fff', flex: 1, margin: 10
        , padding: 10, fontSize: 20
    },
    button: {
        backgroundColor: '#58595A',
        flex: 1,
        height: 45,
        justifyContent: 'center',
        alignItems: 'center',
    }

});
