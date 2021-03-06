package com.applikeysolutions.switchtoolbar.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.applikeysolutions.switchtoolbar.R;

import applikeysolutions.com.switchtoolbar.view.SwitchToolbar;

public class MainActivity extends AppCompatActivity {

    private SwitchToolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (SwitchToolbar) findViewById(R.id.toolbar);

        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("TEST TEST TEST");
    }
}
