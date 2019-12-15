package com.example.iotcloudsmartfarm;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.apicall.GetLog;
import com.example.iotcloudsmartfarm.data.Tag;
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

import java.util.ArrayList;


public class DataViewFragment extends Fragment {

    View view;
    String startDate;
    String endDate;
    TextView startDateLabel;
    TextView endDateLabel;
    //api url
    private final static String URL = "https://4xc9g8j5ud.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010/data";

    public LineChart lineChart;
    //radinbutton에 매칭시킬 열거형
    enum Type {TEMPERATURE("온도"), HUMIDITY("습도"), SOILHUMIDITY("토양수분량"), SUN("햇빛");

        private final  String name;
        private Type(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }
    public DataViewFragment() {
        // Required empty public constructor
    }
    //인스턴스 생성 및 반환
    public static DataViewFragment newInstance(){
        DataViewFragment fg = new DataViewFragment();
        return fg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_view, container, false);
        startDate = "";
        endDate = "";
        startDateLabel= (TextView) view.findViewById(R.id.start_date);
        endDateLabel = (TextView) view.findViewById(R.id.end_date);
        Button dateChoiceBtn = (Button) view.findViewById(R.id.date_choice_btn);
        Button requestBtn = (Button)view.findViewById(R.id.request_btn);
        lineChart = (LineChart) view.findViewById(R.id.chart);

        //날짜 선택 버튼 이벤트
        //시작 날짜 및 시간 dialog 종료 날짜 및 시간 dialog 호출
        dateChoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DatePIckerDialog는 날짜 선택을 위해 이용
                DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        startDate += String.format("%d-%d-%d", i, i1+1, i2);
                    }
                };
                DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        endDate += String.format("%d-%d-%d", i, i1+1, i2);
                    }
                };
                //TimePickerDialog는 시간 선택을 위해 이용
                //한자리숫자 데이터에 대해서 0을 붙이는 처리 추가
                TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String minute;
                        if(i1 >=10)
                            minute = i1+"";
                        else
                            minute = "0" + i1;
                        startDate += String.format(" %d:%s", i, minute);
                        startDateLabel.setText(startDate);
                    }
                };
                TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String minute;
                        if(i1 >=10)
                            minute = i1+"";
                        else
                            minute = "0" + i1;
                        endDate += String.format(" %d:%s", i, minute);
                        endDateLabel.setText(endDate);
                    }
                };

                TimePickerDialog endTimePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, endTimeSetListener, 18, 20, true);
                endTimePickerDialog.setTitle("종료 시간");
                endTimePickerDialog.show();

                DatePickerDialog endDateDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, endDateSetListener, 2019, 11, 15);
                endDateDialog.setTitle("종료 날짜");
                endDateDialog.show();


                TimePickerDialog startTimePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, startTimeSetListener, 18, 10, true);
                startTimePickerDialog.setTitle("시작 시간");
                startTimePickerDialog.show();

                DatePickerDialog startDateDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,startDateSetListener, 2019, 11, 15);
                startDateDialog.setTitle("시작 날짜");
                startDateDialog.show();

                startDate = "";
                endDate = "";
            }
        });


        //조회 버튼이 눌렸을때 로그조회 async실행 및 차트 그리기 시작
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup);
                RadioButton rb = (RadioButton) view.findViewById(rg.getCheckedRadioButtonId());
                String type = rb.getText().toString();
                String tagName = null;
                //눌린 라디오 버튼 체크
                if(type.equals(Type.TEMPERATURE.getName())){
                    tagName = "temperature";
                }
                else if(type.equals(Type.HUMIDITY.getName())){
                    tagName = "humidity";
                }
                else if(type.equals(Type.SOILHUMIDITY.getName())){
                    tagName = "soilMoisture";
                }
                else if(type.equals(Type.SUN.getName())){
                    tagName = "sunlight";
                }
                else {
                    Toast.makeText(getActivity(), "조회할 태그를 골라주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //그래프 그리기
                makeGraph(tagName);
            }
        });

        return view;
    }

    private void makeGraph(String tagName){
        //asyncTask 시작
        new GetLog((MainActivity)getActivity(), this, startDateLabel.getText().toString(), endDateLabel.getText().toString(), tagName ,URL).execute();

        //각 데이터에 맞는 라벨로 변경해줌
        TextView textView = (TextView) view.findViewById(R.id.log_label);
        if(tagName.equals("temperature")){
            textView.setText("온도 - 단위(°C)");
        }
        else if(tagName.equals("humidity")){
            textView.setText("습도 - 단위(%)");
        }
        else if(tagName.equals("soilMoisture")){
            textView.setText("토양수분량 - 단위(%)");
        }
        else if(tagName.equals("sunlight")){
            textView.setText("태양광 - 단위(%)");
        }

    }




}
