package com.example.iotcloudsmartfarm;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ResourceBundle;


/**
 * A simple {@link Fragment} subclass.
 */
public class ControlDataViewFragment extends Fragment {

    View view;

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
        return view;
    }

}
