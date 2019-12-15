package com.example.iotcloudsmartfarm;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.apicall.GetThingShadow;
import com.example.iotcloudsmartfarm.apicall.UpdateShadow;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * 디바이스 현재 상태 조회 및 모터 제어 프래그먼트
 */
public class ControlFragment extends Fragment {

    View view;
    private TextView dateLabel;
    private TextView temperatureLabel;
    private TextView humidityLabel;
    private TextView soilMoistureLabel;
    private TextView sunLightLabel;
    private TextView waterMotorLabel;
    private TextView sunvisorLabel;
    private TextView referenceLabel;

    public Map<String, String> state;
    //api url정의
    private static final String SHADOWURL = "https://4xc9g8j5ud.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010";

    public ControlFragment() {
        // Required empty public constructor
    }
    public static ControlFragment newInstance(){
        ControlFragment fg = new ControlFragment();
        return fg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control, container, false);

        dateLabel = (TextView) view.findViewById(R.id.date_label);
        temperatureLabel = (TextView) view.findViewById(R.id.text_temperature);
        humidityLabel = (TextView) view.findViewById(R.id.text_humidity);
        soilMoistureLabel = (TextView) view.findViewById(R.id.text_soilmoisture);
        sunLightLabel = (TextView) view.findViewById(R.id.text_sunlight);
        waterMotorLabel = (TextView) view.findViewById(R.id.text_watermotor);
        sunvisorLabel = (TextView) view.findViewById(R.id.text_sunvisor);

        Button refreshButton = (Button) view.findViewById(R.id.refresh_btn);
        //데이터 갱신
        refreshDataDeviceInfo();

        //갱신 버튼 누를시 데이터 갱신
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshDataDeviceInfo();
            }
        });

        Button sunvisorOpenButton = (Button) view.findViewById(R.id.btn_open);
        Button sunvisorHalfOpenButton = (Button) view.findViewById(R.id.btn_halfopen);
        Button sunvisorCloseButton = (Button) view.findViewById(R.id.btn_close);
        //서보모터 제어를 위한 3개의 버튼
        //각각 OPEN, HALFOPEN, CLOSE를 담당
        sunvisorOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSunVisor("OPEN");
                sunvisorLabel.setText("OPEN");
            }
        });
        sunvisorHalfOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSunVisor("HALFOPEN");
                sunvisorLabel.setText("HALFOPEN");
            }
        });
        sunvisorCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSunVisor("CLOSE");
                sunvisorLabel.setText("CLOSE");
            }
        });
        return view;
    }
    private void refreshDataDeviceInfo(){
        //asyncTask시작 (디바이스 섀도우 정보를 가져옴)
        new GetThingShadow((MainActivity)getActivity(), this, SHADOWURL).execute();
        //현재 시간을 가져와서 label에 set
        Date mDate = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = "최근 갱신 : " + simpleDateFormat.format(mDate);

        if(state == null){
            Toast.makeText(getActivity(), "정보 갱신에 실패하였습니다", Toast.LENGTH_SHORT).show();
            clearData();
            return;
        }
        //가져온 데이터를 바탕으로 라벨 새로고침
        dateLabel.setText(getTime);
        temperatureLabel.setText(state.get("temperature"));
        humidityLabel.setText(state.get("humidity"));
        soilMoistureLabel.setText(state.get("soilMoisture"));
        sunLightLabel.setText(state.get("sunlight"));
        waterMotorLabel.setText(state.get("watermotor"));
        sunvisorLabel.setText(state.get("sunvisor"));
    }
    //라벨 초기화
    private void clearData(){
        dateLabel.setText("");
        temperatureLabel.setText("");
        humidityLabel.setText("");
        soilMoistureLabel.setText("");
        sunLightLabel.setText("");
        waterMotorLabel.setText("");
        sunvisorLabel.setText("");
    }
    //서보모터 제어버튼 클릭시 호출
    private void setSunVisor(String state){
        //받아온 값을 바탕으로 json 생성
        JSONObject payload= new JSONObject();
        try{
            JSONObject tag = new JSONObject();
            tag.put("tagName", "sunvisor");
            tag.put("tagValue", state);

            payload.put("tag", tag);
        }catch(JSONException e){
            e.printStackTrace();
        }
        //디바이스 상태 변경을 위한 api 호출
        new UpdateShadow(getActivity(), this, SHADOWURL).execute(payload);
    }
}
