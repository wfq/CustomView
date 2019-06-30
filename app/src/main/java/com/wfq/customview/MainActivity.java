package com.wfq.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.CycleInterpolator;

import com.wfq.customview.widget.RoundRippleButton;

public class MainActivity extends AppCompatActivity {

    RoundRippleButton rippleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rippleButton = findViewById(R.id.rippleBtn);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleButton.setSelected(true);
            }
        });
    }
}
