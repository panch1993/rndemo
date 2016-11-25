/**
 * Watermark
 * Created by panchenhuan on 16/10/21.
 */
'use strict';
import React, {Component} from 'react';
import {
    View,
    Text,
    StyleSheet, TouchableOpacity, NativeAppEventEmitter, Image, Dimensions, NativeModules
} from 'react-native';
import TextField from 'react-native-md-textinput';
var watermark = NativeModules.Watermark;
var TX_A = 'tx_iA';
var TX_B = 'tx_iB';
var height = Dimensions.get('window').height;
var width = Dimensions.get('window').width;
export default class Watermark extends Component {
    componentDidMount() {
        NativeAppEventEmitter.addListener("textBase64", (str64)=> {
            var base64Icon = 'data:image/png;base64,' + str64.base64;
            console.log('textBase64', str64);
            this.setState({imgUri: base64Icon});
            // this.setState({base64:"http://img5q.duitang.com/uploads/item/201501/25/20150125222801_UQnn2.jpeg"});
        });
        NativeAppEventEmitter.addListener("imageBase64", (str64)=> {
            var base64Icon = 'data:image/png;base64,' + str64.base64;
            this.setState({imgUri: base64Icon});
            // this.setState({base64:"http://img5q.duitang.com/uploads/item/201501/25/20150125222801_UQnn2.jpeg"});
            console.log('imageBase64', str64.base64);
        });
    }

    constructor(props) {
        super(props);
        this.state = {//设置初值
            bgUri: 'null',
            iconUri: 'null',
            imgUri: 'null',
        };
    }

    _textInput(str, refs) {
        return (<TextField
            wrapperStyle={styles.search}
            inputStyle={styles.textInputs}
            label={str}
            labelColor={"#C18F13"}
            highlightColor={'#C18F13'}
            textColor={"#C18F13"}
            textBlurColor={"#C18F13"}
            borderColor={"#C18F13"}
            ref={refs}//this.refs[TX_IN].state.text
        />);
    }

    _mix() {
        let a = this.state.bgUri;
        let b = this.state.iconUri;
        if ((a == 'null' | '') || (b == '' | 'null')) {
            return;
        }
        watermark.creatWatermarkByImg(a, b);
    }

    _preview() {
        this.setState({
            bgUri: this.refs[TX_A].state.text === '' ? 'null' : this.refs[TX_A].state.text,
            iconUri: this.refs[TX_B].state.text === '' ? 'null' : this.refs[TX_B].state.text,
        })
    }

    render() {
        return (
            <View style={{flex: 1}}>
                <View style={[styles.viewPart, {paddingBottom: 10}]}>
                    <Image style={styles.imgBorder}
                           source={{uri: this.state.bgUri}}/>
                    <View style={{justifyContent: 'space-between', height: 180}}>
                        <Text style={{color: '#C18F13', fontSize: 20, fontWeight: 'bold', textAlign: 'right'}}
                              onPress={this._preview.bind(this)}>Pre{'\n'}view

                        </Text>
                        <Image style={[styles.imgBorder, {width: 55, height: 55}]}
                               source={{uri: this.state.iconUri}}/>
                    </View>
                </View>
                <View style={styles.container}>
                    <Text style={styles.textMix} onPress={this._mix.bind(this)}>Mix.</Text>
                    <TextField
                        wrapperStyle={styles.search}
                        inputStyle={styles.textInputs}
                        label={"Background"}
                        labelColor={"#C18F13"}
                        highlightColor={'#C18F13'}
                        textColor={"#C18F13"}
                        textBlurColor={"#C18F13"}
                        borderColor={"#C18F13"}
                        ref={TX_A}//this.refs[TX_IN].state.text
                    />
                    <TextField
                        wrapperStyle={styles.search}
                        inputStyle={styles.textInputs}
                        label={"Watermark"}
                        labelColor={"#C18F13"}
                        highlightColor={'#C18F13'}
                        textColor={"#C18F13"}
                        textBlurColor={"#C18F13"}
                        borderColor={"#C18F13"}
                        ref={TX_B}//this.refs[TX_IN].state.text
                    />
                </View>
                <View style={[styles.viewPart, {alignItems: 'center'}]}>
                    <Image style={[styles.imgBorder, {width: 0.9 * width, height: 200}]}
                           source={{uri: this.state.imgUri}}>
                    </Image>
                </View>
            </View>
        );
    }
}
const styles = StyleSheet.create({
    container: {
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#213970',
    },
    textMix: {
        width: width,
        textAlign: 'center',
        color: '#C18F13',
        fontSize: 22,
        fontWeight: '500',
    },
    viewPart: {
        backgroundColor: '#B24736',
        flex: 1,
        width: width,
        flexDirection: 'row',
        alignItems: 'flex-end',
        justifyContent: 'space-around',
    },
    search: {
        width: width * 0.8,
    },
    textInputs: {
        fontSize: 12,
        height: 34,
        lineHeight: 34
    },
    imgBorder: {
        borderColor: '#C18F13',
        borderWidth: 3,
        width: 0.75 * width, height: 180,
        backgroundColor: '#C18F13'
    }
});