package com.kanal77.kanal77;

/**
 * Created by Petar on 3/13/2017.
 */

public class CityWeather {

    int city_id;
    String city_name;
    int weather_id;
    String weather_main;
    String weather_desc;
    int temperature;

    public CityWeather(int city_id, String city_name, int weather_id, String weather_main, String weather_desc, int temperature) {
        this.city_id = city_id;
        this.city_name = city_name;
        this.weather_id = weather_id;
        this.weather_main = weather_main;
        this.weather_desc = weather_desc;
        this.temperature = temperature;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(int weather_id) {
        this.weather_id = weather_id;
    }

    public String getWeather_main() {
        return weather_main;
    }

    public void setWeather_main(String weather_main) {
        this.weather_main = weather_main;
    }

    public String getWeather_desc() {
        return weather_desc;
    }

    public void setWeather_desc(String weather_desc) {
        this.weather_desc = weather_desc;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "CityWeather{" +
                "city_id=" + city_id +
                ", city_name='" + city_name + '\'' +
                ", weather_id=" + weather_id +
                ", weather_main='" + weather_main + '\'' +
                ", weather_desc='" + weather_desc + '\'' +
                ", temperature=" + temperature +
                '}';
    }
}
