package com.example.iotcloudsmartfarm.apicall;

import android.app.Activity;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.ControlFragment;
import com.example.iotcloudsmartfarm.httpconnection.PutRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class UpdateShadow extends PutRequest {
    String urlStr;
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
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }
}
