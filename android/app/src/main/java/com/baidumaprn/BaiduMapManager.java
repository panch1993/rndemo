package com.baidumaprn;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidumaprn.util.PoiOverlay;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

/**
 * Created by panchenhuan on 16/10/12.
 */
public class BaiduMapManager extends SimpleViewManager<MapView> implements OnGetPoiSearchResultListener {
    MapView        mMapView;
    BaiduMap       mBaiduMap;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true; // 是否首次定位
    //搜索
    private PoiSearch mPoiSearch = null;

    private Context context;

    @Override
    public String getName() {
        return "BaiduMapView";
    }

    @Override
    protected MapView createViewInstance(ThemedReactContext reactContext) {
        this.context = reactContext;
        isFirstLoc = true;
        mMapView = new MapView(reactContext);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(reactContext);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        return mMapView;
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "search", 2,"location",3
        );
    }

    @Override
    public void receiveCommand(MapView view, int commandId,
                               @Nullable ReadableArray config) {
        if (commandId == 2) {
            if (null != config) {
                String key = config.getString(0);
                PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(key)
                        .sortType(PoiSortType.distance_from_near_to_far)
                        .location(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                        .radius(1500).pageNum(1);
                mPoiSearch.searchNearby(nearbySearchOption);
            }
        } else if (commandId == 3) {
            isFirstLoc = true;
        }
    }

    @ReactProp(name = "showUserLocation", defaultBoolean = true)
    public void showUserLocation(MapView v, @Nullable boolean b) {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(b);
    }

    /**
     * 设置定位模式
     */
    @ReactProp(name = "userTrackingMode")
    public void setLocationMode(MapView view, @Nullable int mode) {
        switch (mode) {
            case 0:
                //普通
                mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case 1:
                //跟随
                mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case 2:
                //罗盘
                mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
        }
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
    }

    //图标点击
    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            return true;
        }
    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(context, "未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(context, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 响应城市内搜索按钮点击事件
     */
    public void searchButtonProcess(View v, String city, String key) {
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city).keyword(key).pageNum(1));
    }

    /**
     * 响应周边搜索按钮点击事件
     */
    @ReactProp(name = "keyword")
    public void searchNearbyProcess(MapView v, @Nullable String key) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(key)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                .radius(1500).pageNum(1);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     *
     * @param result
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(context, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }


    private BDLocation mLocation;

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll)/*.zoom(18.0f)*/;
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            mLocation = location;
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @ReactProp(name = "zoomLevel", defaultFloat = 18.0f)
    public void zoomLevel(MapView v, @Nullable float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(zoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }
}
