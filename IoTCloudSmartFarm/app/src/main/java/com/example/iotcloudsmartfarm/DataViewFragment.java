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
    private final static String URL = "https://4xc9g8j5ud.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010/data";
    public ArrayList<Tag> data;
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


        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup);

        dateChoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        startDate += String.format(" %d:%d", i, i1);
                        startDateLabel.setText(startDate);
                    }
                };
                TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        endDate += String.format(" %d:%d", i, i1);
                        endDateLabel.setText(endDate);
                    }
                };

                TimePickerDialog endTimePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, endTimeSetListener, 0, 0, false);
                endTimePickerDialog.setTitle("종료 시간");
                endTimePickerDialog.show();

                DatePickerDialog endDateDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, endDateSetListener, 2019, 12, 0);
                endDateDialog.setTitle("종료 날짜");
                endDateDialog.show();


                TimePickerDialog startTimePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, startTimeSetListener, 0, 0, false);
                startTimePickerDialog.setTitle("시작 시간");
                startTimePickerDialog.show();

                DatePickerDialog startDateDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,startDateSetListener, 2019, 12, 0);
                startDateDialog.setTitle("시작 날짜");
                startDateDialog.show();

                startDate = "";
                endDate = "";
            }
        });



        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rb = (RadioButton) view.findViewById(rg.getCheckedRadioButtonId());
                String type = rb.getText().toString();
                String tagName = null;

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
                makeGraph(tagName);
            }
        });

        return view;
    }

    private void makeGraph(String tagName){
        new GetLog((MainActivity)getActivity(), this, startDateLabel.getText().toString(), endDateLabel.getText().toString(), URL).execute();

        LineChart lineChart = (LineChart) view.findViewById(R.id.chart);
        ArrayList<Entry> datas = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int length = data.size();
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
            datas.add(new Entry(length-i-1, value));
        }

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

        LineDataSet lineDataSet = new LineDataSet(datas, tagName);
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

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        //x축 레이블 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new GraphAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);


        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText(tagName);

        //차트 각종 설정
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateXY(2000, 2000 , Easing.EasingOption.EaseInCubic, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
        lineChart.getMarker();
        lineChart.setDrawMarkers(true);
    }

    public class GraphAxisValueFormatter implements IAxisValueFormatter {
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


}
