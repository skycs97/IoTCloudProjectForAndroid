package com.example.iotcloudsmartfarm.data;

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
