package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;
import java.util.List;

public class WeatherAddressApi  implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "app-api/dueros/gaode/location-weather-info";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.XD_URL;
    }
    @NonNull
    @Override
    public CacheMode getCacheMode() {
        return CacheMode.NO_CACHE;
    }
    private String fullAddress;

    public WeatherAddressApi(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public class Bean implements Serializable {

        private List<WeatherInfoRespVoDTO> weatherInfoRespVo;
        private List<LocationInfoRespVoDTO> locationInfoRespVo;

        public List<WeatherInfoRespVoDTO> getWeatherInfoRespVo() {
            return weatherInfoRespVo;
        }

        public void setWeatherInfoRespVo(List<WeatherInfoRespVoDTO> weatherInfoRespVo) {
            this.weatherInfoRespVo = weatherInfoRespVo;
        }

        public List<LocationInfoRespVoDTO> getLocationInfoRespVo() {
            return locationInfoRespVo;
        }

        public void setLocationInfoRespVo(List<LocationInfoRespVoDTO> locationInfoRespVo) {
            this.locationInfoRespVo = locationInfoRespVo;
        }

        public class WeatherInfoRespVoDTO implements Serializable{
            private String province;
            private String city;
            private String adcode;
            private String weather;
            private String temperature;
            private String winddirection;
            private String windpower;
            private String humidity;
            private String reporttime;

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getAdcode() {
                return adcode;
            }

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getTemperature() {
                return temperature;
            }

            public void setTemperature(String temperature) {
                this.temperature = temperature;
            }

            public String getWinddirection() {
                return winddirection;
            }

            public void setWinddirection(String winddirection) {
                this.winddirection = winddirection;
            }

            public String getWindpower() {
                return windpower;
            }

            public void setWindpower(String windpower) {
                this.windpower = windpower;
            }

            public String getHumidity() {
                return humidity;
            }

            public void setHumidity(String humidity) {
                this.humidity = humidity;
            }

            public String getReporttime() {
                return reporttime;
            }

            public void setReporttime(String reporttime) {
                this.reporttime = reporttime;
            }
        }

        public class LocationInfoRespVoDTO implements Serializable{
            private String location;
            private String adcode;

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getAdcode() {
                return adcode;
            }

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }
        }
    }
}
