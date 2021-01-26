package com.zawraaadmin.ui.activity_notifications;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zawraaadmin.R;
import com.zawraaadmin.adapters.NotificationAdapter;
import com.zawraaadmin.databinding.ActivityNotificationBinding;
import com.zawraaadmin.language.Language;
import com.zawraaadmin.models.NotificationDataModel;
import com.zawraaadmin.models.NotificationModel;
import com.zawraaadmin.mvp.activity_notification_mvp.ActivityNotificationPresenter;
import com.zawraaadmin.mvp.activity_notification_mvp.ActivityNotificationView;
import com.zawraaadmin.share.Common;
import com.zawraaadmin.tags.Tags;
import com.zawraaadmin.ui.activity_login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class NotificationActivity extends AppCompatActivity implements ActivityNotificationView {

    private ActivityNotificationBinding binding;
    private String lang;
    private List<NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    private ActivityNotificationPresenter presenter;
    private ProgressDialog dialog;
    private int pos = -1;
    private LinearLayoutManager manager;
    private int current_page = 1;
    private boolean isLoading=false;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        initView();

    }

    private void initView() {
        notificationModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        manager = new LinearLayoutManager(this);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(manager);
        binding.recView.setItemViewCacheSize(25);
        binding.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recView.setDrawingCacheEnabled(true);
        adapter = new NotificationAdapter(notificationModelList, this);
        binding.recView.setAdapter(adapter);
        presenter = new ActivityNotificationPresenter(this, this);
        presenter.getNotifications(1);
        binding.llBack.setOnClickListener(view -> finish());
        binding.imagelogout.setOnClickListener(view -> presenter.logout());
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("sksks",dy+"");
                if (dy < 0) {
                    int lastItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_items = adapter.getItemCount();
                    Log.e("kdkdkdk",lastItemPos+" "+total_items);
                    if (!isLoading) {
                        isLoading = true;
                        notificationModelList.add(0, null);
                        adapter.notifyItemInserted(0);
                        int next_page = current_page + 1;
                        presenter.getNotifications(next_page);


                    }
                }
            }
        });
    }

    @Override
    public void onSuccess(NotificationDataModel data) {
        if (data.getData().size() > 0) {
            binding.tvNoData.setVisibility(View.GONE);
            notificationModelList.addAll(data.getData());
            adapter.notifyDataSetChanged();
            current_page=data.getCurrent_page();
        } else {
            binding.tvNoData.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        binding.tvNoData.setVisibility(View.GONE);
        binding.progBar.setVisibility(View.VISIBLE);
        notificationModelList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void hideProgressBar() {
        binding.progBar.setVisibility(View.GONE);

    }

    @Override
    public void onLoad() {
        if (dialog == null) {
            dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
        } else {
            dialog.dismiss();
        }
        dialog.show();
    }

    @Override
    public void onFinishload() {
        dialog.dismiss();
    }

    @Override
    public void onSuccessDelete() {
        notificationModelList.remove(pos);
        adapter.notifyItemRemoved(pos);
    }
    @Override
    public void onNavigateToLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onLogoutSuccess() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager!=null){
            manager.cancel(Tags.not_tag,Tags.not_id);
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void delete(int position) {
        pos = position;
        presenter.deltenotification(notificationModelList.get(position).getId());
    }
}