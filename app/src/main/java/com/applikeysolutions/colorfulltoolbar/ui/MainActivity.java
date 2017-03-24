package com.applikeysolutions.colorfulltoolbar.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.applikeysolutions.colorfulltoolbar.R;
import com.applikeysolutions.colorfulltoolbar.ui.view.ColorfulToolbar;

public class MainActivity extends AppCompatActivity {

    private ColorfulToolbar mToolbar;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (ColorfulToolbar) findViewById(R.id.toolbar);
        mButton = (Button) findViewById(R.id.button);

        initToolbar();
    }

    public void initToolbar() {
        setSupportActionBar(mToolbar);
    }
}
