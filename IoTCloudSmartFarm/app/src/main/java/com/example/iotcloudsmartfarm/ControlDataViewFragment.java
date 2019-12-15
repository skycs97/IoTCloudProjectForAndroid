package com.example.iotcloudsmartfarm;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.iotcloudsmartfarm.apicall.GetControlLog;
import com.example.iotcloudsmartfarm.data.controlTag;

import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * 디바이스 제어 이력 확인
 */
public class ControlDataViewFragment extends Fragment {

    View view;
    String startDate;
    String endDate;
    TextView startDateLabel;
    TextView endDateLabel;
    public ListView listView;
    public ArrayList<controlTag> data;
    //라디오 버튼을 위한 열거형
    enum Type {WATERMOTOR("워터펌프"), SUNVISOR("차양막") ;

        private final  String name;
        private Type(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }
    //url 주소
    private final static String URL = "https://4xc9g8j5ud.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010/controldata";

    public ControlDataViewFragment() {
        // Required empty public constructor
    }

    public static ControlDataViewFragment newInstance(){
        ControlDataViewFragment fg = new ControlDataViewFragment();
        return fg;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control_data_view, container, false);

        listView = (ListView) view.findViewById(R.id.listView);

        startDate = "";
        endDate = "";

        startDateLabel = (TextView) view.findViewById(R.id.start_date_control);
        endDateLabel = (TextView) view.findViewById(R.id.end_date_control);

        Button datechoiceBtn = (Button) view.findViewById(R.id.date_choice_control_btn);
        Button requestBtn = (Button) view.findViewById(R.id.request_control_btn);
        //날짜 선택 버튼 이벤트
        //시작 날짜 및 시간 dialog 종료 날짜 및 시간 dialog 호출
        datechoiceBtn.setOnClickListener(new View.OnClickListener() {
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

                TimePickerDialog endTimePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, endTimeSetListener, 18, 20, false);
                endTimePickerDialog.setTitle("종료 시간");
                endTimePickerDialog.show();

                DatePickerDialog endDateDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, endDateSetListener, 2019, 11, 15);
                endDateDialog.setTitle("종료 날짜");
                endDateDialog.show();


                TimePickerDialog startTimePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, startTimeSetListener, 18, 10, false);
                startTimePickerDialog.setTitle("시작 시간");
                startTimePickerDialog.show();

                DatePickerDialog startDateDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,startDateSetListener, 2019, 11, 15);
                startDateDialog.setTitle("시작 날짜");
                startDateDialog.show();

                startDate = "";
                endDate = "";
            }
        });
        //조회 버튼이 눌렸을때 로그조회 async실행 및 리스트뷰 그리기 시작
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup_control);
                RadioButton rb = (RadioButton) view.findViewById(rg.getCheckedRadioButtonId());
                String type = rb.getText().toString();
                String controlTag = null;
                //라디오 버튼 체크 확인
                if(type.equals(Type.WATERMOTOR.getName()))
                    controlTag = "watermotor";
                else if(type.equals(Type.SUNVISOR.getName()))
                    controlTag = "sunvisor";
                else{
                    Toast.makeText(getActivity(), "조회할 태그를 골라주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //리스트뷰 그리기
                makeList(controlTag);

            }
        });
        return view;
    }

    private void makeList(String tagName){
        //db조회를 위한 asyncTask 실행
        new GetControlLog((MainActivity)getActivity(), this, startDateLabel.getText().toString(), endDateLabel.getText().toString(), URL, tagName).execute();

        TextView textView = (TextView) view.findViewById(R.id.log_control_label);

        if(tagName.equals("watermotor")){
            textView.setText("워터 펌프 ");
        }
        else if(tagName.equals("sunvisor")){
            textView.setText("차양막");
        }
    }

}
