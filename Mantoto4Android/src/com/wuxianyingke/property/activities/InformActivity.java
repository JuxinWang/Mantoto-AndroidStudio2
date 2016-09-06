package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;

public class InformActivity extends BaseActivity {
    private LinearLayout informLinearLayout;
    private Button topbarLeft;
    private TextView topbarTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        setImmerseLayout(findViewById(R.id.common_back));
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarTxt = (TextView) findViewById(R.id.topbar_txt);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topbarTxt.setText("通知");

        informLinearLayout = (LinearLayout) findViewById(R.id.informLinearLayout);
        informLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(InformActivity.this, InformDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}
