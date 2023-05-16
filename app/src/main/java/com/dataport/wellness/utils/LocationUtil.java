package com.dataport.wellness.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class LocationUtil {

    public static Location getLocation(Context context) {

        /*获取LocationManager对象*/
        LocationManager locationManager = (LocationManager)   context.getSystemService(Context.LOCATION_SERVICE);
        String provider = getProvider(locationManager);
        if (provider == null) {
            Toast.makeText(context, "定位失败", Toast.LENGTH_SHORT).show();
        }
        //系统权限检查警告，需要做权限判断
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return null;
        }
        return locationManager.getLastKnownLocation(provider);
    }
    /**
     * 根据LocationManager获取定位信息的提供者
     * @param locationManager
     * @return
     */
    private static String getProvider(LocationManager locationManager){

        //获取位置信息提供者列表
        List<String> providerList = locationManager.getProviders(true);

        if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            //获取NETWORK定位
            return LocationManager.NETWORK_PROVIDER;
        }else if (providerList.contains(LocationManager.GPS_PROVIDER)){
            //获取GPS定位
            return LocationManager.GPS_PROVIDER;
        }
        return null;
    }
}
