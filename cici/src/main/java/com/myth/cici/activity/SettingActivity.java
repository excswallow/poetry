package com.myth.cici.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myth.cici.BaseActivity;
import com.myth.cici.R;
import com.myth.cici.db.YunDatabaseHelper;
import com.myth.poetrycommon.utils.OthersUtils;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

    }

    private void initView() {

        refreshYun();
        refreshTypeface();
        refreshCheck();

        ((TextView) findViewById(R.id.yun_title)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.yun_value)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.typeface_value)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.typeface_title)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.check_value)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.check_title)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.about_title)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.notice_title)).setTypeface(myApplication.getTypeface());

        ((TextView) findViewById(R.id.username_title)).setTypeface(myApplication.getTypeface());
        ((TextView) findViewById(R.id.username_value)).setTypeface(myApplication.getTypeface());

        findViewById(R.id.item_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity).setItems(new String[]{"复制"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                OthersUtils.copy(getString(R.string.about_qq), mActivity);
                                Toast.makeText(mActivity, R.string.about_qq_toast, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).show();

            }
        });

        findViewById(R.id.item_yun).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity).setSingleChoiceItems(YunDatabaseHelper.YUNString,
                        YunDatabaseHelper.getDefaultYunShu(mActivity), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                YunDatabaseHelper.setDefaultYunShu(mActivity, which);
                                refreshYun();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        findViewById(R.id.item_typeface).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity).setSingleChoiceItems(myApplication.TypefaceString,
                        myApplication.getDefaulTypeface(mActivity), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myApplication.setDefaultTypeface(mActivity, which);
                                myApplication.setTypeface(mActivity, myApplication.getDefaulTypeface(mActivity));
                                refreshTypeface();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        findViewById(R.id.item_check).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] s = {mActivity.getString(R.string.check_true), mActivity.getString(R.string.check_false)};
                new AlertDialog.Builder(mActivity).setSingleChoiceItems(s,
                        myApplication.getCheckAble(mActivity) ? 0 : 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myApplication.setCheckAble(mActivity, which == 0);
                                refreshCheck();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        findViewById(R.id.item_about).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, AboutActivity.class));
            }
        });

        final TextView username = (TextView) findViewById(R.id.username_value);
        String name = myApplication.getDefaultUserName(mActivity);
        if (!TextUtils.isEmpty(name)) {
            username.setText(name);
        }

        findViewById(R.id.item_username).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText et = new EditText(mActivity);
                new AlertDialog.Builder(mActivity).setTitle("请输入用户名").setIcon(android.R.drawable.ic_dialog_info).setView(
                        et).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username.setText(et.getText().toString().trim());
                        myApplication.setDefaultUserName(mActivity, et.getText().toString().trim());
                    }
                }).setNegativeButton("取消", null).show();
            }
        });
    }

    private void refreshYun() {
        ((TextView) findViewById(R.id.yun_value)).setText(YunDatabaseHelper.YUNString[YunDatabaseHelper.getDefaultYunShu(mActivity)]);
    }

    private void refreshTypeface() {
        ((TextView) findViewById(R.id.typeface_value)).setText(myApplication.TypefaceString[myApplication.getDefaulTypeface(mActivity)]);
    }

    private void refreshCheck() {
        if (myApplication.getCheckAble(mActivity)) {
            ((TextView) findViewById(R.id.check_value)).setText(R.string.check_true);
        } else {
            ((TextView) findViewById(R.id.check_value)).setText(R.string.check_false);
        }
    }

}
