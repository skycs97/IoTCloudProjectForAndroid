package com.example.iotcloudsmartfarm;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//tablayout을 위한 프래그먼트 관리 어댑터
public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                return ControlFragment.newInstance();
            case 1:
                return DataViewFragment.newInstance();
            case 2:
                return ControlDataViewFragment.newInstance();
                default:
                    return null;
        }
    }

    @Override
    public int getCount(){
        return 3;
    }
}
