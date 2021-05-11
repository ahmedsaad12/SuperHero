package com.zawraaadmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;


import com.zawraaadmin.R;
import com.zawraaadmin.databinding.SpinnerRowBinding;
import com.zawraaadmin.models.PharmacyModel;

import java.util.List;

public class ClientSpinnerAdapter extends BaseAdapter {
    private List<PharmacyModel> list;
    private Context context;
    private LayoutInflater inflater;

    public ClientSpinnerAdapter(List<PharmacyModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row,null,false);
        PharmacyModel model = list.get(i);
        binding.setTitle(model.getTitle());
        return binding.getRoot();
    }
}
