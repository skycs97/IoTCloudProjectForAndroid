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
 * A simple {@link Fragment} subclass.
 */
public class ControlDataViewFragment extends Fragment {

    View view;
    String startDate;
    String endDate;
    TextView startDateLabel;
    TextView endDateLabel;
    ListView listView;
    public ArrayList<controlTag> data;
    enum Type {WATERMOTOR("워터펌프"), SUNVISOR("차양막") ;

        private final  String name;
        private Type(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }
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

        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup_control);

        datechoiceBtn.setOnClickListener(new View.OnClickListener() {
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
                String controlTag = null;

                if(type.equals(Type.WATERMOTOR.getName()))
                    controlTag = "watermotor";
                else if(type.equals(Type.SUNVISOR.getName()))
                    controlTag = "sunvisor";
                else{
                    Toast.makeText(getActivity(), "조회할 태그를 골라주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                makeList(controlTag);

            }
        });
        return view;
    }

    private void makeList(String tagName){
        new GetControlLog((MainActivity)getActivity(), this, startDateLabel.getText().toString(), endDateLabel.getText().toString(), URL);
        TextView textView = (TextView) view.findViewById(R.id.log_label);
        if(tagName.equals("watermotor")){
            textView.setText("워터 펌프 ");
        }
        else if(tagName.equals("sunvisor")){
            textView.setText("차양막");
        }

        MyAdapter myAdapter = new MyAdapter(data);

        listView.setAdapter(myAdapter);
        listView.setDividerHeight(5);
    }

}
