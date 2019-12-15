package com.example.iotcloudsmartfarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.iotcloudsmartfarm.data.controlTag;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private ArrayList<controlTag> datas = new ArrayList<>();

    public MyAdapter(ArrayList<controlTag> datas){
        this.datas = datas;
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_custom, viewGroup, false);
        }

        TextView dataNameLabel = (TextView) view.findViewById(R.id.control_dataname);
        TextView dataStateLabel = (TextView) view.findViewById(R.id.state_label);
        TextView dataTimeLabel = (TextView) view.findViewById(R.id.time_label);

        dataNameLabel.setText(datas.get(i).dataname);
        dataStateLabel.setText(datas.get(i).state);
        dataTimeLabel.setText(datas.get(i).timestamp);

        return view;
    }
}
