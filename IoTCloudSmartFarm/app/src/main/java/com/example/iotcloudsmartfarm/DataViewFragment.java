package com.example.iotcloudsmartfarm;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DataViewFragment extends Fragment {

    View view;
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

        return view;
    }


}
