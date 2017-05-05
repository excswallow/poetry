//package com.myth.cici.activity;
//
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import com.myth.cici.R;
//import com.myth.cici.entity.Cipai;
//import com.myth.cici.fragment.ChangeBackgroundFragment;
//import com.myth.cici.fragment.ChangePictureFragment;
//import com.myth.cici.fragment.EditFragment;
//import com.myth.poetrycommon.BaseActivity;
//import com.myth.poetrycommon.BaseApplication;
//import com.myth.poetrycommon.Constant;
//import com.myth.poetrycommon.db.WritingDatabaseHelper;
//import com.myth.poetrycommon.entity.Writing;
//import com.myth.poetrycommon.utils.FileUtils;
//import com.myth.poetrycommon.utils.ResizeUtils;
//import com.myth.poetrycommon.utils.StringUtils;
//import com.myth.poetrycommon.view.GCDialog;
//import com.myth.poetrycommon.view.GCDialog.OnCustomDialogListener;
//import com.myth.poetrycommon.view.TouchEffectImageView;
//
//import java.io.File;
//import java.util.ArrayList;
//
//public class EditActivity extends BaseActivity {
//
//    private Cipai cipai;
//
//    private Writing writing;
//
//    ChangeBackgroundFragment changeBackgroundFrament;
//
//    EditFragment editFragment;
//
//    ChangePictureFragment changePictureFragment;
//
//    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
//
//    private int currentIndex = 0;
//
//    private String oldText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit);
//
//        cipai = (Cipai) getIntent().getSerializableExtra("cipai");
//        writing = (Writing) getIntent().getSerializableExtra("writing");
//
//        // 填新词
//        if (cipai != null) {
//            writing = new Writing();
//            writing.id=writing.hashCode();
//            writing.(cipai.getId());
//            writing.bgimg=("0");
//            writing.former=(cipai);
//        }
//        // 旧词编辑
//        else if (writing != null) {
//            cipai = writing.getCipai();
//        } else {
//            finish();
//            Toast.makeText(this, "进入编辑出错", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (TextUtils.isEmpty(writing.author)) {
//            writing.author = BaseApplication.getDefaultUserName();
//        }
//        if (TextUtils.isEmpty(writing.title)) {
//            writing.title = cipai.name;
//        }
//        WritingDatabaseHelper.generateText(writing);
//        oldText = writing.text;
//        getBottomLeftView().setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                exit();
//            }
//        });
//        setBottomGone();
//
//        ImageView down = new TouchEffectImageView(mActivity, null);
//        down.setImageResource(R.drawable.done);
//        down.setScaleType(ScaleType.CENTER);
//        addBottomRightView(down);
//        down.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (currentIndex == 1) {
//                    changeBackgroundFrament.save();
//                } else if (currentIndex == 2) {
//                    changePictureFragment.save();
//                } else if (currentIndex == 0) {
//                    editFragment.save();
//                }
//                save();
//                writing.setBitmap(null);
//                Intent intent = new Intent(mActivity, ShareActivity.class);
//                intent.putExtra("writing", writing);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        initView();
//    }
//
//    private void initView() {
//
//        final ImageView edit = new TouchEffectImageView(mActivity, null);
//        edit.setScaleType(ScaleType.FIT_XY);
//        edit.setImageResource(R.drawable.layout_bg_edit_selected);
//
//        final ImageView background = new TouchEffectImageView(mActivity, null);
//        background.setScaleType(ScaleType.FIT_XY);
//        background.setImageResource(R.drawable.layout_bg_paper);
//
//        final ImageView picture = new TouchEffectImageView(mActivity, null);
//        picture.setScaleType(ScaleType.FIT_XY);
//        picture.setImageResource(R.drawable.layout_bg_album);
//
//        edit.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                changeFragment(0);
//                edit.setImageResource(R.drawable.layout_bg_edit_selected);
//                background.setImageResource(R.drawable.layout_bg_paper);
//                picture.setImageResource(R.drawable.layout_bg_album);
//
//            }
//        });
//
//        background.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                changeFragment(1);
//                edit.setImageResource(R.drawable.layout_bg_edit);
//                background.setImageResource(R.drawable.layout_bg_paper_selected);
//                picture.setImageResource(R.drawable.layout_bg_album);
//            }
//        });
//
//        picture.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                changeFragment(2);
//                edit.setImageResource(R.drawable.layout_bg_edit);
//                background.setImageResource(R.drawable.layout_bg_paper);
//                picture.setImageResource(R.drawable.layout_bg_album_sel);
//            }
//        });
//
//        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ResizeUtils.getInstance().dip2px(40),
//                ResizeUtils.getInstance().dip2px(45));
//        lps.leftMargin = 20;
//        addBottomCenterView(edit, lps);
//        addBottomCenterView(background, lps);
//        addBottomCenterView(picture, lps);
//
//        // 创建修改实例
//        editFragment = EditFragment.getInstance(writing);
//        changeBackgroundFrament = ChangeBackgroundFragment.getInstance(writing);
//        changePictureFragment = ChangePictureFragment.getInstance(writing);
//
//        fragments.add(editFragment);
//        fragments.add(changeBackgroundFrament);
//        fragments.add(changePictureFragment);
//        changeFragment(currentIndex);
//    }
//
//    public void changeFragment(int pos) {
//        currentIndex = pos;
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, fragments.get(pos));
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//
//    public void exit() {
//        if (currentIndex == 1) {
//            changeBackgroundFrament.save();
//        } else if (currentIndex == 2) {
//            changePictureFragment.save();
//        }
//        editFragment.save();
//
//        if (!StringUtils.isEmpty(writing.text) && !writing.text.replaceAll("\\\\n", "").equals(oldText.replaceAll("\\\\n", ""))) {
//            Bundle bundle = new Bundle();
//            bundle.putString(GCDialog.DATA_CONTENT, mActivity.getString(R.string.save_content));
//            bundle.putString(GCDialog.DATA_TITLE, mActivity.getString(R.string.save_title));
//            bundle.putString(GCDialog.CONFIRM_TEXT, mActivity.getString(R.string.save));
//            bundle.putString(GCDialog.CANCEL_TEXT, mActivity.getString(R.string.give_up));
//            new GCDialog(mActivity, new OnCustomDialogListener() {
//
//                @Override
//                public void onConfirm() {
//                    save();
//                    finish();
//                }
//
//                @Override
//                public void onCancel() {
//                    finish();
//                }
//            }, bundle).show();
//        } else {
//            finish();
//        }
//    }
//
//    private void save() {
//        if (!StringUtils.isNumeric(writing.bgimg) && writing.bitmap != null) {
//            File file = new File(Constant.BACKGROUND_DIR, writing.bitmap.hashCode() + "");
//            FileUtils.saveBitmap(writing.bitmap, file);
//            writing.setBgimg(file.getAbsolutePath());
//        }
//        WritingDatabaseHelper.saveWriting(writing);
//    }
//
//    @Override
//    public void onBackPressed() {
//        exit();
//    }
//
//}
