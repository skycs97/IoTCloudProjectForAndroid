package com.example.iotcloudsmartfarm.apicall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.ControlFragment;
import com.example.iotcloudsmartfarm.httpconnection.GetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarException;

public class GetThingShadow extends GetRequest {
    String urlStr;
    ProgressDialog progressDialog;
    ControlFragment controlFragment;

    public GetThingShadow(Activity activity, ControlFragment fragment, String urlStr){
        super(activity);
        this.urlStr = urlStr;
        controlFragment = fragment;
    }

    @Override
    protected void onPreExecute(){
        progressDialog  = ProgressDialog.show(activity, "데이터 받아오는중", "잠시만 기다려 주세요.", true, false);

        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e){
            Toast.makeText(activity, "오류 발생 : " + urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String jsonString){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        if(jsonString == null){
            return;
        }
        controlFragment.state = getStateFromJSONString(jsonString);

    }

    protected Map<String, String> getStateFromJSONString(String jsonString){
        Map<String, String> output = new HashMap<>();
        try{
            jsonString = jsonString.substring(1,jsonString.length()-1);
            jsonString = jsonString.replace("\\\"", "\"");

            JSONObject root = new JSONObject(jsonString);
            JSONObject reported = root.getJSONObject("state").getJSONObject("reported");

            String temperature = reported.getString("temperature");
            String hummidity = reported.getString("humidity");
            String soilMoisture = reported.getString("soilMoisture");
            String sunlight = reported.getString("sunlight");
            String watermotor = reported.getString("watermotor");
            String sunvisor = reported.getString("sunvisor");

            output.put("temperature", temperature);
            output.put("humidity", hummidity);
            output.put("soilMoisture", soilMoisture);
            output.put("sunlight", sunlight);
            output.put("watermotor", watermotor);
            output.put("sunvisor", sunvisor);
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return output;
    }
}
