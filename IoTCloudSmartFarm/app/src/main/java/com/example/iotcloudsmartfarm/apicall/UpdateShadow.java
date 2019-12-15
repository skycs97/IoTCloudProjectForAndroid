package com.example.iotcloudsmartfarm.apicall;

import android.app.Activity;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.ControlFragment;
import com.example.iotcloudsmartfarm.httpconnection.PutRequest;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 디바이스 상태 변경을 위한 AsynTask
 */
public class UpdateShadow extends PutRequest {
    String urlStr;
    //호출한 프래그먼트 저장
    ControlFragment controlFragment;
    public UpdateShadow(Activity activity, ControlFragment fragment, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
        controlFragment = fragment;
    }

    @Override
    protected void onPreExecute(){
        try{
            url = new URL(urlStr);
        } catch (MalformedURLException e){
            e.printStackTrace();
            Toast.makeText(activity, "오류 발생 : " + urlStr, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostExecute(String result){
        //Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }
}
