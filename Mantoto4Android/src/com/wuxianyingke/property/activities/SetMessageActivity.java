package com.wuxianyingke.property.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;

import java.io.FileOutputStream;


public class SetMessageActivity extends BaseActivity {
    // 顶部导航
    private TextView topbar_txt,topbar_right;
    private Spinner spinnerArrowAge;
    private Button topbar_left;
    private int favorite_flag;
    private int index;
    //上中下三个控件
    private TextView personalTxtAge,personalTxtGender,personalTxtPlotcontrol;
    private Spinner spinnerArrowGender;

    private LinearLayout plot_control;
    //弹出年龄按钮集合
    private Button[] agebtns = new Button[6];
    private LocalStore localstore;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showBackDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_message);
        setImmerseLayout(findViewById(R.id.common_back));
        initwidget();


        topbar_right.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                /*Intent mIntent = new Intent(SetMessageActivity.this,UserCenterActivity.class);
                mIntent.putExtra("gender",personalTxtGender.getText().toString());
                mIntent.putExtra("age",personalTxtAge.getText().toString());
                //设置结果并进行传送
                SetMessageActivity.this.setResult(RESULT_OK,mIntent);
*/
            }



        });


    }

    public void initwidget(){
        //标题
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_right = (TextView) findViewById(R.id.topbar_right);
        topbar_right.setVisibility(View.VISIBLE);
        topbar_right.setText("保存");
        topbar_right.setTextColor(Color.rgb(255,165,0));
        personalTxtGender = (TextView) findViewById(R.id.personal_txt_gender);
        personalTxtAge = (TextView) findViewById(R.id.personal_txt_age);


        //--中间标题
        topbar_txt.setText("个人信息");
        //-- 左侧返回菜单处理事件
        topbar_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                 // TODO Auto-generated method stub
                 if (0 != favorite_flag) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setClass(getApplicationContext(),
                            AddressActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });

        topbar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(personalTxtGender.getText().toString().equals("")&&
                        personalTxtAge.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "性别/年龄不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    localstore.addPersonalMessage(SetMessageActivity.this,
                            personalTxtAge.getText().toString(),
                            personalTxtGender.getText().toString());
                    Toast.makeText(getApplicationContext(),
                            "保存成功",Toast.LENGTH_SHORT);
                }


            }
        });

        //年龄下拉列表
        spinnerArrowAge = (Spinner) findViewById(R.id.spinner_arrow_age);
        spinnerArrowAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] ages = getResources().getStringArray(R.array.ages);
                personalTxtAge.setText(ages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //性别下拉列表
        spinnerArrowGender = (Spinner) findViewById(R.id.spinner_arrow_gender);
        spinnerArrowGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] genders = getResources().getStringArray(R.array.genders);

                personalTxtGender.setText(genders[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        //小区管理
        plot_control = (LinearLayout) findViewById(R.id.plot_control);
        plot_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(getApplicationContext(),LocationActivity.class);
                startActivity(intent);
            }
        });
        personalTxtPlotcontrol = (TextView) findViewById(R.id.personal_txt_plotcontrol);



    }


   /* private void showPopwindowAge() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindow_age,null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(SetMessageActivity.this.findViewById(R.id.arrow_age),
                Gravity.BOTTOM,0,0);
        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Toast.makeText(getApplicationContext(),"消失",Toast.LENGTH_SHORT);
            }
        });
    }*/

    private void showBackDialog() {
        if (TextUtils.isEmpty(personalTxtAge.getText().toString().trim())
                && TextUtils
                .isEmpty(personalTxtGender.getText().toString().trim())
                && TextUtils.isEmpty(personalTxtPlotcontrol.getText().toString().trim())
               ) {
            SetMessageActivity.this.finish();
        } else {
            new AlertDialog.Builder(SetMessageActivity.this)
                    .setMessage("是否放弃编辑")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    SetMessageActivity.this.finish();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(true).show();
        }
    }


}
