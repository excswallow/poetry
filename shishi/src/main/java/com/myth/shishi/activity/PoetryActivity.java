package com.myth.shishi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.myth.poetrycommon.BaseActivity;
import com.myth.poetrycommon.BaseApplication;
import com.myth.poetrycommon.activity.ShareEditActivity;
import com.myth.poetrycommon.utils.OthersUtils;
import com.myth.poetrycommon.utils.ResizeUtils;
import com.myth.poetrycommon.utils.StringUtils;
import com.myth.poetrycommon.view.CircleImageView;
import com.myth.poetrycommon.view.TouchEffectImageView;
import com.myth.shishi.R;
import com.myth.shishi.db.AuthorDatabaseHelper;
import com.myth.shishi.db.PoetryDatabaseHelper;
import com.myth.shishi.entity.Author;
import com.myth.shishi.entity.Poetry;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class PoetryActivity extends BaseActivity {

    private ArrayList<Poetry> mList = new ArrayList<>();

    private TextView content;

    private Poetry poetry;

    private TextView title;

    CircleImageView shareView;

    private Author author;

    private PopupWindow menu;

    int[] location;

    private View menuView;

    private TouchEffectImageView more;

    private TextToSpeech mSpeech;

    private boolean mTTSEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poetry);


        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    } else {
                        mTTSEnable = true;
                        mSpeech.setSpeechRate(0.8f);
                    }
                }
            }
        });


        setBottomVisible();

        mList = PoetryDatabaseHelper.getRandom200();
        getRandomPoetry();
        initView();
    }

    @Override
    protected void onDestroy() {
        if (mSpeech != null) {
            mSpeech.stop();
            mSpeech.shutdown();
        }
        super.onDestroy();
    }

    private int color;

    private void getRandomPoetry() {
        poetry = mList.get(new Random().nextInt(mList.size()));
        author = AuthorDatabaseHelper.getAuthorByName(poetry.author);
        color = BaseApplication.instance.getRandomColor();
    }

    private void initView() {
        LinearLayout topView = (LinearLayout) findViewById(R.id.right);
        LayoutParams param = new LayoutParams(
                ResizeUtils.getInstance().dip2px(80), ResizeUtils.getInstance().dip2px(
                80));
        shareView = new CircleImageView(mActivity, color,
                R.drawable.share3_white);
        topView.addView(shareView, 1, param);

        shareView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareEditActivity.class);
                intent.putExtra("data", poetry.toWriting());
                startActivity(intent);
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(BaseApplication.instance.getTypeface());
        title.setText(poetry.author);

        content = (TextView) findViewById(R.id.content);
        content.setTypeface(BaseApplication.instance.getTypeface());
        ((TextView) findViewById(R.id.note)).setTypeface(BaseApplication.instance
                .getTypeface());

        ((TextView) findViewById(R.id.author)).setTypeface(BaseApplication.instance
                .getTypeface());

        title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, AuthorPageActivity.class);
                author.color = color;
                intent.putExtra("author", author);
                startActivity(intent);

            }
        });

        initBottomRightView();
        initContentView();
    }

    private void initBottomRightView() {
        ImageView view = new TouchEffectImageView(mActivity, null);
        view.setImageResource(R.drawable.random);
        view.setScaleType(ScaleType.CENTER);
        addBottomRightView(view);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getRandomPoetry();
                refreshRandomView();
            }
        });

        more = new TouchEffectImageView(mActivity, null);
        more.setImageResource(R.drawable.setting);
        more.setScaleType(ScaleType.FIT_XY);
        addBottomRightView(more);
        more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenu();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSpeech != null) {
            mSpeech.stop();
        }
    }

    private void refreshRandomView() {
        title.setText(poetry.author);
        setColor();
        initContentView();
    }

    private void setColor() {
        shareView.setColor(color);
    }

    private void initContentView() {

        String note = poetry.intro;
        ((TextView) findViewById(R.id.note)).setText(note);

        poetry.title = poetry.title.replaceAll("（.*）", "").trim();
        poetry.poetry = poetry.poetry.replaceAll("【.*】", "").trim();
        poetry.poetry = StringUtils.autoLineFeed(poetry.poetry);
        content.setText(poetry.poetry);
        ((TextView) findViewById(R.id.author))
                .setText(poetry.getShowTitle() + "\n");
        setTextSize();

    }

    public void isAddTextSize(boolean add) {
        int size = BaseApplication.getDefaultTextSize(mActivity);
        if (add) {
            size += 2;
        } else {
            size -= 2;
        }
        BaseApplication.setDefaultTextSize(mActivity, size);
        setTextSize();
    }

    public void setTextSize() {

        int size = BaseApplication.getDefaultTextSize(mActivity);
        ((TextView) findViewById(R.id.author)).setTextSize(size);
        content.setTextSize(size);
        ((TextView) findViewById(R.id.note)).setTextSize(size - 2);
    }

    private void showMenu() {
        if (menu == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            menuView = inflater.inflate(R.layout.dialog_poetry, null);

            // PopupWindow定义，显示view，以及初始化长和宽
            menu = new PopupWindow(menuView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // 必须设置，否则获得焦点后页面上其他地方点击无响应
            menu.setBackgroundDrawable(new BitmapDrawable());
            // 设置焦点，必须设置，否则listView无法响应
            menu.setFocusable(true);
            // 设置点击其他地方 popupWindow消失
            menu.setOutsideTouchable(true);

            menu.setAnimationStyle(R.style.popwindow_anim_style);

            // 让view可以响应菜单事件
            menuView.setFocusableInTouchMode(true);

            menuView.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_MENU) {
                        if (menu != null) {
                            menu.dismiss();
                        }
                        return true;
                    }
                    return false;
                }
            });
            location = new int[2];

            menuView.findViewById(R.id.tv1).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            isAddTextSize(true);
                            if (menu != null) {
                                menu.dismiss();
                            }
                        }
                    });
            menuView.findViewById(R.id.tv2).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            isAddTextSize(false);
                            if (menu != null) {
                                menu.dismiss();
                            }
                        }
                    });
            TextView collect = (TextView) menuView.findViewById(R.id.tv3);
            if (PoetryDatabaseHelper.isCollect(poetry.poetry)) {
                collect.setText("取消收藏");
            } else {
                collect.setText("收藏");
            }
            collect.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean isCollect = PoetryDatabaseHelper.isCollect(poetry.id);
                    PoetryDatabaseHelper.updateCollect(poetry.id,
                            !isCollect);
                    if (isCollect) {
                        Toast.makeText(mActivity, "已取消收藏", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(mActivity, "已收藏", Toast.LENGTH_LONG)
                                .show();
                    }
                    if (menu != null) {
                        menu.dismiss();
                    }
                }
            });
            menuView.findViewById(R.id.tv4).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity,
                                    PoetrySearchActivity.class);
                            intent.putExtra("author", author);
                            mActivity.startActivity(intent);
                            if (menu != null) {
                                menu.dismiss();
                            }
                        }
                    });
            menuView.findViewById(R.id.tv5).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (menu != null) {
                                menu.dismiss();
                            }
                            OthersUtils.goBaike(mActivity,poetry.author);
                        }
                    });
            menuView.findViewById(R.id.tv6).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (menu != null) {
                                menu.dismiss();
                            }
                            OthersUtils.goBaike(mActivity,poetry.title);
                        }
                    });
            menuView.findViewById(R.id.tv7).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            OthersUtils.copy(
                                    title.getText() + "\n" + content.getText(),
                                    mActivity);
                            Toast.makeText(mActivity, R.string.copy_text_done,
                                    Toast.LENGTH_SHORT).show();
                            if (menu != null) {
                                menu.dismiss();
                            }
                        }
                    });

            menuView.findViewById(R.id.tv8).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mTTSEnable) {
                                mSpeech.speak(poetry.getShowTitle().replaceAll("\\[.*\\]", "").replaceAll("（.*）", "").replaceAll("【.*】", "") + "\n" + poetry.poetry.replaceAll("[\\[\\]0-9]", "").replaceAll("【.*】", ""), TextToSpeech.QUEUE_FLUSH,
                                        null);
                            } else {
                                Toast.makeText(mActivity, R.string.tts_unable,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            menuView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int popupWidth = menuView.getMeasuredWidth();
            int popupHeight = menuView.getMeasuredHeight();

            more.getLocationOnScreen(location);

            location[0] = location[0] + more.getWidth() / 2 - popupWidth / 2;
            location[1] = location[1] - popupHeight;

            menu.showAtLocation(more, Gravity.NO_GRAVITY, location[0],
                    location[1]);
            // 显示在某个位置

        } else {
            TextView collect = (TextView) menuView.findViewById(R.id.tv3);
            if (PoetryDatabaseHelper.isCollect(poetry.id)) {
                collect.setText("取消收藏");
            } else {
                collect.setText("收藏");
            }
            menu.showAtLocation(more, Gravity.NO_GRAVITY, location[0],
                    location[1]);
        }

    }

}
