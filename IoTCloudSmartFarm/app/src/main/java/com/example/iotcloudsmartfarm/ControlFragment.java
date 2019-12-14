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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
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

    private static final String THINGSHADOWURL = "https://4xc9g8j5ud.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMRKWiFi1010";

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

        refreshDataDeviceInfo();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshDataDeviceInfo();
            }
        });



        return view;
    }
    private void refreshDataDeviceInfo(){
        new GetThingShadow(getActivity(), this, THINGSHADOWURL).execute();
        Date mDate = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = "최근 갱신 : " + simpleDateFormat.format(mDate);

        if(state == null){
            Toast.makeText(getActivity(), "정보 갱신에 실패하였습니다", Toast.LENGTH_SHORT).show();
            clearData();
            return;
        }
        dateLabel.setText(getTime);
        temperatureLabel.setText(state.get("temperature"));
        humidityLabel.setText(state.get("humidity"));
        soilMoistureLabel.setText(state.get("soilMoisture"));
        sunLightLabel.setText(state.get("sunlight"));
        waterMotorLabel.setText(state.get("watermotor"));
        sunvisorLabel.setText(state.get("sunvisor"));
    }

    private void clearData(){
        dateLabel.setText("");
        temperatureLabel.setText("");
        humidityLabel.setText("");
        soilMoistureLabel.setText("");
        sunLightLabel.setText("");
        waterMotorLabel.setText("");
        sunvisorLabel.setText("");
    }
}
