package com.example.android.actionbarcompat.basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by joanne on 4/9/15.
 */
public class JobItemAdapter extends ArrayAdapter<JobItem> {

    int resource;

    public JobItemAdapter(Context ctx, int res, List<JobItem> items)
    {
        super(ctx, res, items);
        resource = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout jobView;
        JobItem jb = getItem(position);

        if (convertView == null) {
            jobView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, jobView, true);
        } else {
            jobView = (LinearLayout) convertView;
        }

        TextView addrView = (TextView) jobView.findViewById(R.id.address_text);
        TextView dateView = (TextView) jobView.findViewById(R.id.date_text);
        TextView paidView = (TextView) jobView.findViewById(R.id.paid_view);

        addrView.setText(jb.getWhere());
        dateView.setText(jb.getWhen());
        paidView.setText(jb.getPaid()==1 ? "PAID" : "unpaid");

        return jobView;
    }

}
