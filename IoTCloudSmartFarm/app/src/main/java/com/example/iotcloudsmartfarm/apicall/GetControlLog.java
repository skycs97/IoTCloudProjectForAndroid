package com.example.iotcloudsmartfarm.apicall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.ControlDataViewFragment;
import com.example.iotcloudsmartfarm.MyAdapter;
import com.example.iotcloudsmartfarm.data.Tag;
import com.example.iotcloudsmartfarm.data.controlTag;
import com.example.iotcloudsmartfarm.httpconnection.GetRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 제어 이력 조회를 위한 AsyncTask
 */
public class GetControlLog extends GetRequest {
    String urlStr;
    String startTime;
    String endTime;
    ProgressDialog progressDialog;
    ControlDataViewFragment fg;
    String type;

    public GetControlLog(Activity activity, ControlDataViewFragment fragment, String startTime, String endTime, String urlStr, String type){
        super(activity);
        this.urlStr = urlStr;
        fg = fragment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }
    //조회 끝날때 까지 잠시 대기를 위한 progressDialog
    @Override
    protected void onPreExecute(){
        progressDialog = ProgressDialog.show(activity, "그래프 그리는 중", "잠시만 기다려 주세요", true, false);
        try{
            //api요청을 url형태로 지정
            String params = String.format("?from=%s:00&to=%s:00", startTime, endTime);
            url = new URL(urlStr+params);
        } catch (MalformedURLException e){
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
            Toast.makeText(activity, "데이터를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
            fg.data = null;
            return;
        }
        //결과 데이터를 ArrayList형태로 저장
        ArrayList<controlTag> data = getArrayListFromJSONString(jsonString);

        //adapter에 데이터 연결
        MyAdapter myAdapter = new MyAdapter(data);
        //리스트 뷰에 어댑터 연결 및 구분자 설정
        fg.listView.setAdapter(myAdapter);
        fg.listView.setDividerHeight(5);
    }
    //json형태의 데이터를 ArrayList로 변경후 저장
    protected ArrayList<controlTag> getArrayListFromJSONString(String jsonString){
        ArrayList<controlTag> output = new ArrayList<>();
        try{
            jsonString = jsonString.substring(1, jsonString.length()-1);
            jsonString = jsonString.replace("\\\"", "\"");

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                controlTag thing = new controlTag(jsonObject.getString("dataname"),
                        jsonObject.getString("state"),
                        jsonObject.getString("timestamp"));

                if(jsonObject.getString("dataname").equals(type))
                    output.add(thing);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return output;

    }
}
