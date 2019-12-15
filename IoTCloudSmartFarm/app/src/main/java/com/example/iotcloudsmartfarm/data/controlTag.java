package com.example.iotcloudsmartfarm.data;

//제어 이력 조회를 위한 클래스
public class controlTag {
    public String dataname;
    public String state;
    public String timestamp;

    public controlTag(String dataname, String state, String timestamp) {
        this.dataname = dataname;
        this.state = state;
        this.timestamp = timestamp;
    }
}
