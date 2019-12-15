package com.example.iotcloudsmartfarm.data;

//센서 데이터 조회를 위한 클래스
public class Tag{
    public  String temperature;
    public String humidity;
    public String soilMoisture;
    public String sunlight;
    public String timestamp;
    public Tag(String temperature, String humidity, String soilMoisture, String sunlight, String timestamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.soilMoisture = soilMoisture;
        this.sunlight = sunlight;
        this.timestamp = timestamp;
    }
}
