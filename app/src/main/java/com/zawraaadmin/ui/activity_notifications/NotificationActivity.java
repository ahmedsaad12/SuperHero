package com.zawraaadmin.ui.activity_notifications;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zawraaadmin.R;
import com.zawraaadmin.adapters.ClientSpinnerAdapter;
import com.zawraaadmin.adapters.DelegeteSpinnerAdapter;
import com.zawraaadmin.adapters.NotificationAdapter;
import com.zawraaadmin.adapters.PharmcyAutoAdapter;
import com.zawraaadmin.databinding.ActivityNotificationBinding;
import com.zawraaadmin.language.Language;
import com.zawraaadmin.models.NotModel;
import com.zawraaadmin.models.NotificationDataModel;
import com.zawraaadmin.models.NotificationModel;
import com.zawraaadmin.models.PharmacyModel;
import com.zawraaadmin.models.SingleUserModel;
import com.zawraaadmin.mvp.activity_notification_mvp.ActivityNotificationPresenter;
import com.zawraaadmin.mvp.activity_notification_mvp.ActivityNotificationView;
import com.zawraaadmin.share.Common;
import com.zawraaadmin.tags.Tags;
import com.zawraaadmin.ui.activity_login.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private boolean isLoading = false;
    private List<SingleUserModel> singleUserModelList;
    private List<PharmacyModel> pharmacyModelList;
    private String client_id, delegete_id;
    private String date;
    private String query = "all";
    private PharmcyAutoAdapter pharmcyAutoAdapter;
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
        singleUserModelList = new ArrayList<>();
        pharmacyModelList = new ArrayList<>();
        EventBus.getDefault().register(this);
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
        presenter.getNotifications(1, client_id, delegete_id, date);
        binding.flDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showDateDialog(getFragmentManager());
            }
        });
        pharmcyAutoAdapter = new PharmcyAutoAdapter(this, R.layout.pharmcy_auto_row, pharmacyModelList);

        binding.edtSearch.setAdapter(pharmcyAutoAdapter);
        binding.llBack.setOnClickListener(view -> finish());
        binding.imagelogout.setOnClickListener(view -> presenter.logout());
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    int lastItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_items = adapter.getItemCount();
                    if (!isLoading) {
                        isLoading = true;
                        notificationModelList.add(null);
                        adapter.notifyItemInserted(notificationModelList.size() - 1);
                        int next_page = current_page + 1;
                        presenter.getNotifications(next_page, client_id, delegete_id, date);


                    }
                }
            }
        });
        binding.spinnerdelegete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    delegete_id = singleUserModelList.get(i).getId() + "";
                    notificationModelList.clear();
                    adapter.notifyDataSetChanged();
                    presenter.getNotifications(1, client_id, delegete_id, date);
                } else {
                    delegete_id = null;
                    notificationModelList.clear();
                    adapter.notifyDataSetChanged();
                    presenter.getNotifications(1, client_id, delegete_id, date);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        binding.spinnerclient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//     if(i!=0) {
//         client_id = pharmacyModelList.get(i).getId() + "";
//         notificationModelList.clear();
//         adapter.notifyDataSetChanged();
//         presenter.getNotifications(1, client_id, delegete_id, date);
//     }else {
//         client_id =null;
//         notificationModelList.clear();
//         adapter.notifyDataSetChanged();
//         presenter.getNotifications(1, client_id, delegete_id, date);
//     }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        presenter.getUsers();
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            //    Log.e("dlldl",s+"");
                query = binding.edtSearch.getText().toString();
                if (query.isEmpty() || s.length() == 0) {
                    query = "all";
                    current_page = 1;
                    client_id=null;
                    presenter.getNotifications(current_page, client_id, delegete_id, date);                }
                presenter.search(query);

            }
        });
        presenter.search(query);
    }

    @Override
    public void onSuccess(NotificationDataModel data) {
        if (notificationModelList.size() > 0) {
            if (notificationModelList.get(notificationModelList.size() - 1) == null) {
                notificationModelList.remove(notificationModelList.size() - 1);
                adapter.notifyItemRemoved(notificationModelList.size() - 1);
            }
        }
        if (data.getData().size() > 0) {
            binding.tvNoData.setVisibility(View.GONE);
            notificationModelList.addAll(data.getData());
            adapter.notifyDataSetChanged();
            current_page = data.getCurrent_page();
        } else {
            if (notificationModelList.size() == 0) {
                binding.tvNoData.setVisibility(View.VISIBLE);
            }

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
        if (manager != null) {
            manager.cancel(Tags.not_tag, Tags.not_id);
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserSuccess(List<SingleUserModel> data) {
        singleUserModelList.add(new SingleUserModel(getResources().getString(R.string.choose_delegete)));
        singleUserModelList.addAll(data);
        DelegeteSpinnerAdapter delegeteSpinnerAdapter = new DelegeteSpinnerAdapter(singleUserModelList, this);
        binding.spinnerdelegete.setAdapter(delegeteSpinnerAdapter);

    }

    @Override
    public void onClientSuccess(List<PharmacyModel> data) {
        pharmacyModelList.clear();
        //pharmacyModelList.add(new PharmacyModel(getResources().getString(R.string.choose_client)));
        pharmacyModelList.addAll(data);
        ClientSpinnerAdapter clientSpinnerAdapter = new ClientSpinnerAdapter(pharmacyModelList, this);
//        binding.spinnerclient.setAdapter(clientSpinnerAdapter);
        pharmcyAutoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDateSelected(String date) {
        this.date = date;
        binding.tvDate.setText(date);
        presenter.getNotifications(current_page, client_id, delegete_id, date);
    }

    public void delete(int position) {
        pos = position;
        presenter.deltenotification(notificationModelList.get(position).getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNewMessage(NotModel notModel) {
        notificationModelList.clear();
        adapter.notifyDataSetChanged();
        current_page = 1;
        presenter.getNotifications(current_page, client_id, delegete_id, date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void additem(PharmacyModel pharmacyModel) {
        notificationModelList.clear();
        adapter.notifyDataSetChanged();
        client_id = pharmacyModel.getId() + "";
        presenter.getNotifications(1, client_id, delegete_id, date);
       binding.edtSearch.setText("");
       binding.edtSearch.setText(pharmacyModel.getTitle()+" ");


    }
}