/**
 * Read
 * Created by panchenhuan on 16/10/20.
 */
'use strict';
import React, {Component} from 'react';
import {
    View, Dimensions,
    Text, Image, StyleSheet, ScrollView
} from 'react-native';

var width = Dimensions.get('window').width;
var height = Dimensions.get('window').height;
var SCRV = "scrollview";
var Icon = require('react-native-vector-icons/Entypo');
export default class Read extends Component {

    // 构造
    constructor(props) {
        super(props);
        // 初始状态
        this.state = {
            title: '',
            content: '',
            author:'',
            date:''
        };
    }

    componentWillMount() {
        this.getToday();
    }
    getToday(){
        //获取今天日期
        var date = new Date();
        var today = ""+date.getFullYear()+
            ((date.getMonth()+1)<10?'0'+(date.getMonth()+1):(date.getMonth()+1))+
            (date.getDate()<10?'0'+date.getDate():date.getDate());
        this._net('http://api.meiriyiwen.com/v2/day?date=' + today + '&version=4');
    }
    getArticel() {
        //随机文章
        this._net('http://api.meiriyiwen.com/v2/random?version=4');
    }
    _net(http){
        fetch(http)
            .then((response) => response.json())
            .then((responseJson) => {
                this.setState({
                    title: responseJson.title,
                    content: (responseJson.content.replace(/<.*?>/ig, "       ")),//替换全局标签
                    author:responseJson.author,
                    date:responseJson.date
                })
                return responseJson;
            })
            .catch((error) => {
                console.error(error);
            });
    }
    onpenDrawer() {
        // alert(this.props.openDrawer);
        this.props.openDrawer();
    }
    // 渲染
    render() {
        return (
            <View style={{flex: 1}}>
                <Image style={styles.headImg}
                       source={{uri: 'http://pic56.nipic.com/file/20141221/16450890_184400958000_2.jpg'}}>
                    <Icon
                        style={ {position: 'absolute', left: 8, top: 25}}
                        name="menu"   //图片名连接,可以到这个网址搜索:http://ionicons.com/, 使用时:去掉前面的 "icon-" !!!!
                        size={30}   //图片大小
                        color="white"  //图片颜色
                        onPress={this.onpenDrawer.bind(this)}
                    />
                    <Text style={[styles.headText, {color:'#1A0502'}] } onPress={()=> {
                        this.getToday()
                        //滑动到顶部
                        this.refs[SCRV].scrollTo({x: 0, y: 0, animated: true});
                    }}>Today</Text>
                    <Text style={styles.headText} onPress={()=> {
                        this.getArticel();
                        this.refs[SCRV].scrollTo({x: 0, y: 0, animated: true});
                    }}>Random</Text>
                </Image>
                <Image source={{uri: 'null'}}
                       style={{flex: 1, backgroundColor: '#F6F6F6'}}
                       resizeMethod={'scale'}>
                    <ScrollView ref={SCRV}>
                        <Text style={styles.title}>{this.state.title}</Text>
                        <Text style={styles.author}>{this.state.author}</Text>
                        <Text style={styles.content}>{this.state.content}</Text>
                        <Text style={styles.author}>{this.state.date}</Text>
                    </ScrollView>
                </Image>
            </View>
        )
            ;
    }
}
const styles = StyleSheet.create({
    headImg: {
        width: width,
        height: height * 0.15,
        alignItems: 'flex-end',
        justifyContent: 'space-between',
        flexDirection:'row'
    },
    headText: {
        color: 'white',
        fontSize: 18,
        fontWeight: 'bold',
        margin: 5
    },
    title: {
        color: '#000000',
        fontSize: 20,
        margin: 10,
        textAlign: 'center'
    },
    content: {
        textAlign: 'left',
        margin: 10,
        letterSpacing:2,
        lineHeight:30
    },
    author:{
        textAlign:'center',
        fontSize:10
    }
});