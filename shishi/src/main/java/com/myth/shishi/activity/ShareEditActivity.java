package com.myth.shishi.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.myth.poetrycommon.BaseActivity;
import com.myth.poetrycommon.utils.FileUtils;
import com.myth.poetrycommon.utils.StringUtils;
import com.myth.poetrycommon.view.TouchEffectImageView;
import com.myth.shishi.Constant;
import com.myth.shishi.R;
import com.myth.shishi.entity.Poetry;
import com.myth.shishi.entity.Writing;
import com.myth.shishi.fragment.ChangeBackgroundFragment;
import com.myth.shishi.fragment.ChangePictureFragment;

import java.io.File;
import java.util.ArrayList;

public class ShareEditActivity extends BaseActivity {

    private Writing writing;

    private Poetry poetry;

    ChangeBackgroundFragment changeBackgroundFragment;

    ChangePictureFragment changePictureFragment;

    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setBottomVisible();

        poetry = (Poetry) getIntent().getSerializableExtra("data");

        writing = new Writing();
        writing.setText(poetry.getPoetry().replaceAll("[\\[\\]0-9]", ""));
        writing.setTitle(poetry.getTitle());
        writing.setAuthor(poetry.getAuthor());

        ImageView down = new TouchEffectImageView(mActivity, null);
        down.setImageResource(R.drawable.done);
        down.setScaleType(ScaleType.FIT_XY);
        addBottomRightView(down, new LayoutParams(60, 60));
        down.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentIndex == 0) {
                    changeBackgroundFragment.save();
                } else {
                    changePictureFragment.save();
                }
                if (!StringUtils.isNumeric(writing.getBgimg()) && writing.getBitmap() != null) {
                    String filename = writing.getBitmap().hashCode() + "";
                    File file = new File(Constant.SHARE_DIR, filename);
                    FileUtils.saveBitmap(writing.getBitmap(), file);
                    writing.setBgimg(file.getAbsolutePath());
                }
                writing.setBitmap(null);
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("writing", writing);
                startActivity(intent);
                finish();
            }
        });

        initView();
    }

    private void initView() {

        final ImageView background = new TouchEffectImageView(mActivity, null);
        background.setScaleType(ScaleType.FIT_XY);
        background.setImageResource(R.drawable.layout_bg_paper_selected);

        final ImageView picture = new TouchEffectImageView(mActivity, null);
        picture.setScaleType(ScaleType.FIT_XY);
        picture.setImageResource(R.drawable.layout_bg_album);

        background.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeFragment(0);
                background.setImageResource(R.drawable.layout_bg_paper_selected);
                picture.setImageResource(R.drawable.layout_bg_album);
            }
        });

        picture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeFragment(1);
                background.setImageResource(R.drawable.layout_bg_paper);
                picture.setImageResource(R.drawable.layout_bg_album_sel);
            }
        });

        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(99, 114);
        lps.leftMargin = 20;
        addBottomCenterView(background, lps);
        addBottomCenterView(picture, lps);

        changeBackgroundFragment = ChangeBackgroundFragment.getInstance(writing);
        changePictureFragment = ChangePictureFragment.getInstance(writing);

        fragments.add(changeBackgroundFragment);
        fragments.add(changePictureFragment);
        changeFragment(currentIndex);
    }

    public void changeFragment(int pos) {
        currentIndex = pos;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragments.get(pos));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
