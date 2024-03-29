package com.example.iotcloudsmartfarm.apicall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.iotcloudsmartfarm.DataViewFragment;
import com.example.iotcloudsmartfarm.R;
import com.example.iotcloudsmartfarm.data.Tag;
import com.example.iotcloudsmartfarm.httpconnection.GetRequest;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 센서 데이터 조회를 위한 AsyncTask
 */
public class GetLog extends GetRequest {
    String urlStr;

    String startTime;
    String endTime;
    ProgressDialog progressDialog;
    DataViewFragment fg;
    String tagName;

    //생성자에 조회 시작 시간, 조회 종료 시간 및 조회 태그명 추가
    public GetLog(Activity activity, DataViewFragment fragment, String startTime, String endTime, String tagName,String urlStr){
        super(activity);
        this.urlStr = urlStr;
        fg = fragment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tagName = tagName;
    }
    //조회 끝날때 까지 잠시 대기를 위한 progressDialog
    @Override
    protected void onPreExecute(){
        progressDialog = ProgressDialog.show(activity, "그래프 그리는 중", "잠시만 기다려 주세요", true, false);
        try{
            //api조건식을 url형태로 변경
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
            return;
        }
        //json형식의 결과 데이터를 ArrayList형태로 저장
        ArrayList<Tag> data = getArrayListFromJSONString(jsonString);

        //각각 차트에 들어갈 x,y값 및 라벨
        ArrayList<Entry> datas = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        if(data == null){
            Toast.makeText(activity,"정보조회 실패", Toast.LENGTH_SHORT).show();
            return;
        }
        int length = data.size();
        //DB에 한 레코드에 네가지 정보를 다 저장하여 전부 가져오기 때문에
        //필요한 정보만 뽑아서 entry형태로 저장
        for(int i=0; i< length; i++){
            Tag item = data.get(i);
            float value = 0.0f;
            switch (tagName){
                case "temperature":
                    value = Float.parseFloat(item.temperature);
                    break;
                case "humidity":
                    value = Float.parseFloat(item.humidity);
                    break;
                case "soilMoisture":
                    value = Float.parseFloat(item.soilMoisture);
                    break;
                case "sunlight":
                    value = Float.parseFloat(item.sunlight);
                    break;
            }
            labels.add(item.timestamp);
            datas.add(new Entry(i, value));
        }

        //차트를 그리기위한 다양한 설정 - 데이터셋에 대하여
        LineDataSet lineDataSet = new LineDataSet(datas, "");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(true);
        //데이타셋을 라인차트 데이타에 연결
        LineData lineData = new LineData(lineDataSet);
        fg.lineChart.setData(lineData);

        //x축 레이블 설정
        XAxis xAxis = fg.lineChart.getXAxis();
        //커스텀 포매터로 x축의 label을 재정의
        xAxis.setValueFormatter(new GraphAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        //y축설정
        YAxis yLAxis = fg.lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);


        YAxis yRAxis = fg.lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText(tagName);

        //차트 각종 설정
        fg.lineChart.setDoubleTapToZoomEnabled(false);
        fg.lineChart.setDrawGridBackground(false);
        fg.lineChart.setDescription(description);
        fg.lineChart.animateXY(2000, 2000 , Easing.EasingOption.EaseInCubic, Easing.EasingOption.EaseInCubic);
        fg.lineChart.invalidate();
        fg.lineChart.getMarker();
        fg.lineChart.setDrawMarkers(true);
    }
    //받아온 json데이터를 ArrayList형태로 변경
    protected ArrayList<Tag> getArrayListFromJSONString(String jsonString){
        ArrayList<Tag> output = new ArrayList<>();
        try{
            jsonString = jsonString.substring(1, jsonString.length()-1);
            jsonString = jsonString.replace("\\\"", "\"");

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                Tag thing = new Tag(jsonObject.getString("temperature"),
                        jsonObject.getString("humidity"),
                        jsonObject.getString("soilMoisture"),
                        jsonObject.getString("sunlight"),
                        jsonObject.getString("timestamp"));

                output.add(thing);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return output;
    }


}
//MPAndroidChart의 X축 label을 커스텀
class GraphAxisValueFormatter implements IAxisValueFormatter {
    private ArrayList<String> mValues;
    // 생성자 초기화
    GraphAxisValueFormatter(ArrayList<String> values){
        mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis){
        return mValues.get((int)value);
    }
}

