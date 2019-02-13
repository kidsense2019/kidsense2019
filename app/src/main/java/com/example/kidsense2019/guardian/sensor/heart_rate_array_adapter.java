package com.example.kidsense2019.guardian.sensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kidsense2019.R;

import java.util.ArrayList;
import java.util.List;

public class heart_rate_array_adapter extends ArrayAdapter {
    List list = new ArrayList<>();

    public heart_rate_array_adapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(heart_rate_data_struct object) {
        list.add(object);

        super.add(object);
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ProductHolder productHolder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.heart_rate_layout,parent,false);
            productHolder = new ProductHolder();
            productHolder.txHeartRate = (TextView)row.findViewById(R.id.heartRate);
            productHolder.txdate = (TextView)row.findViewById(R.id.dateData);
            productHolder.txtime = (TextView)row.findViewById(R.id.timeData);
            row.setTag(productHolder);

        }
        else {
            productHolder = (ProductHolder)row.getTag();
        }

        heart_rate_data_struct heartRate = (heart_rate_data_struct) getItem(position);
        productHolder.txHeartRate.setText(heartRate.getHeartRate());
        productHolder.txdate.setText(heartRate.getDate());
        productHolder.txtime.setText(heartRate.getTime());

        return row;
    }

    static class ProductHolder  {

        TextView txHeartRate, txdate, txtime;

    }

    @Override
    public void clear() {
        list.clear();
    }

    public void setList(List list) {
        this.list = list;
    }

}
