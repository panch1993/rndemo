/**
 * HomePage
 * Created by panchenhuan on 16/10/19.
 */
'use strict';
import React, {Component} from 'react';
import {
    View, Image, StyleSheet,
    Text, Dimensions, Animated, PanResponder
} from 'react-native';


var width = Dimensions.get('window').width;
var height = Dimensions.get('window').height;
var Icon = require('react-native-vector-icons/Entypo');
export default class HomePage extends Component {

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
            fadeAnim: new Animated.Value(1.0)
        };
    }

    startAnimation() {
        this.state.currentAlpha = this.state.currentAlpha == 1.0 ? 0.0 : 1.0;
        Animated.timing(
            this.state.fadeAnim,
            {toValue: this.state.currentAlpha}
        ).start();
    }

    onpenDrawer() {
        // alert(this.props.openDrawer);
        this.props.openDrawer();
    }
    // 渲染
    render() {
        return (
            <View style={{flex: 1}}>
                <Image style={styles.backgroundImg}
                       resizeMethod={'scale'}
                       source={{uri: 'http://image.tianjimedia.com/uploadImages/2011/293/EGR7LZ363FQ7.jpg'}}>
                    <Icon
                        style={ {position: 'absolute', left: 8, top: 25}}
                        name="menu"   //图片名连接,可以到这个网址搜索:http://ionicons.com/, 使用时:去掉前面的 "icon-" !!!!
                        size={30}   //图片大小
                        color="white"  //图片颜色
                        onPress={this.onpenDrawer.bind(this)}
                    />
                    <Animated.Image style={[styles.headImg, {
                        opacity: this.state.fadeAnim, //透明度动画
                        transform: [//transform动画
                            {
                                translateY: this.state.fadeAnim.interpolate({
                                    inputRange: [0, 1],
                                    outputRange: [60, 0] //线性插值，0对应60，0.6对应30，1对应0
                                }),
                            },
                            {
                                scale: this.state.fadeAnim
                            },
                        ],
                    }]}
                                    source={{uri: 'http://cdn.duitang.com/uploads/item/201512/16/20151216203703_4ZJcU.thumb.224_0.jpeg'}}/>
                    <Text style={styles.nickName} onPress={()=> this.startAnimation()}>Panc_</Text>
                </Image>
                <View style={styles.container}>

                </View>
            </View>
        );
    }
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    backgroundImg: {
        width: width,
        height: height * 0.4,
        alignItems: 'center',
        justifyContent: 'flex-end',

    },
    headImg: {
        width: 80,
        height: 80,
        borderRadius: 90,
        borderWidth: 2,
        marginBottom: 5,
        borderColor: 'white'
    },
    nickName: {
        color: 'white',
        fontSize: 18,
        marginBottom: 5,
        fontWeight: 'bold'
    }
});