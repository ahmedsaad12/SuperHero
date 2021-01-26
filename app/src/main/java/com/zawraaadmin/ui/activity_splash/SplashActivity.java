package com.zawraaadmin.ui.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;


import com.zawraaadmin.R;
import com.zawraaadmin.databinding.ActivitySplashBinding;
import com.zawraaadmin.language.Language;
import com.zawraaadmin.mvp.activity_splash_mvp.SplashPresenter;
import com.zawraaadmin.mvp.activity_splash_mvp.SplashView;
import com.zawraaadmin.preferences.Preferences;
import com.zawraaadmin.ui.activity_login.LoginActivity;
import com.zawraaadmin.ui.activity_notifications.NotificationActivity;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity implements SplashView {
    private ActivitySplashBinding binding;
    private SplashPresenter presenter;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase,Paper.book().read("lang","ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Transition transition = new TransitionSet();
            transition.setInterpolator(new LinearInterpolator());
            transition.setDuration(500);
            getWindow().setEnterTransition(transition);
            getWindow().setExitTransition(transition);

        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        initView();
    }

    private void initView() {
        presenter = new SplashPresenter(this,this);
        preferences = Preferences.getInstance();
    }


    @Override
    public void onNavigateToLanguageActivity() {

    }

    @Override
    public void onNavigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNavigateToHomeActivity() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
        finish();
    }





}