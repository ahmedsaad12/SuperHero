package com.zawraaadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.zawraaadmin.R;
import com.zawraaadmin.models.PharmacyModel;
import com.zawraaadmin.ui.activity_notifications.NotificationActivity;

import java.util.List;

public class PharmcyAutoAdapter extends ArrayAdapter<PharmacyModel> {
    private final Context mContext;
    private final List<PharmacyModel> PharmacyModelList;
    private final int mLayoutResourceId;

    public PharmcyAutoAdapter(Context context, int resource, List<PharmacyModel> PharmacyModelList) {
        super(context, resource, PharmacyModelList);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.PharmacyModelList=PharmacyModelList;
    }

    public int getCount() {
        return PharmacyModelList.size();
    }

    public PharmacyModel getItem(int position) {
        return PharmacyModelList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            PharmacyModel PharmacyModel = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.tvTitle);
            name.setText(PharmacyModel.getTitle());
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mContext instanceof NotificationActivity){
                        NotificationActivity notificationActivity=(NotificationActivity) mContext;
                        notificationActivity.additem(PharmacyModelList.get(position));

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }}