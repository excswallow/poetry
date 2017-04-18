package com.myth.cici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myth.cici.R;
import com.myth.cici.db.CiDatabaseHelper;
import com.myth.cici.entity.Ci;
import com.myth.cici.entity.Cipai;
import com.myth.cici.wiget.CircleTextView;
import com.myth.cici.wiget.SwitchPoint;
import com.myth.poetrycommon.BaseActivity;
import com.myth.poetrycommon.BaseApplication;
import com.myth.poetrycommon.utils.DisplayUtil;
import com.myth.poetrycommon.utils.ResizeUtil;

import java.util.ArrayList;

public class CipaiActivity extends BaseActivity {

    private ArrayList<Ci> ciList = new ArrayList<Ci>();

    private Cipai cipai;

    private ViewPager gallery;

    SwitchPoint switchPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipai);

        if (getIntent().hasExtra("cipai")) {
            cipai = (Cipai) getIntent().getSerializableExtra("cipai");
        }

        ArrayList<Ci> cis = CiDatabaseHelper.getCiByCipaiId(cipai.getId());
        Ci intro = new Ci();
        intro.setText(cipai.getSource());
        ciList.add(intro);
        ciList.addAll(cis);
        initView();
    }

    private void initView() {
        TextView writeTV = new TextView(mActivity);
        writeTV.setText("填词");
        writeTV.setTextSize(18);
        writeTV.setPadding(15, 15, 15, 15);
        writeTV.setTypeface(BaseApplication.instance.getTypeface());
        writeTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mActivity, EditActivity.class);
                intent.putExtra("cipai", cipai);
                startActivity(intent);
            }
        });
        addBottomRightView(writeTV, new LayoutParams(-2, -2));

        int color = BaseApplication.getColorById(cipai
                .getColor_id());

        LinearLayout topView = (LinearLayout) findViewById(R.id.top);

        android.widget.LinearLayout.LayoutParams param = new android.widget.LinearLayout.LayoutParams(
                DisplayUtil.dip2px(mActivity, 64), DisplayUtil.dip2px(
                        mActivity, 64));

        String count = cipai.getWordcount() + "";
        if (cipai.getWordcount() < 100) {
            count = "0" + cipai.getWordcount();
        }
        topView.addView(new CircleTextView(mActivity, count, color), param);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(BaseApplication.instance.getTypeface());
        title.setTextSize(44);
        title.setText(cipai.getName());

        if (cipai.getName().length() > 5) {
            TextView title1 = (TextView) findViewById(R.id.title1);
            title1.setTypeface(BaseApplication.instance.getTypeface());
            title1.setTextSize(44);

            title1.setText(cipai.getName().substring(0, 5));
            title.setText(cipai.getName().substring(5));
        }
        gallery = (ViewPager) findViewById(R.id.gc_main_gallery);

        switchPoint = (SwitchPoint) findViewById(R.id.gc_main_dot);

        // 释放触摸控制，并且设置点点
        gallery.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                gallery.getParent().requestDisallowInterceptTouchEvent(true);
                if (ciList != null && ciList.size() > 0) {
                    switchPoint.setSelectedSwitchBtn(arg0 % ciList.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        /**
         * 滚图
         */
        if (ciList != null && ciList.size() > 0) {
            switchPoint.addSwitchBtn(ciList.size(),
                    R.drawable.gc_cover_switcher_dot,
                    ResizeUtil.resize(10),
                    ResizeUtil.resize(10));
            switchPoint.setSelectedSwitchBtn(0);
            gallery.setCurrentItem(0);
            gallery.setAdapter(galleryAdapter);
            galleryAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 滚图的adapter
     */
    private PagerAdapter galleryAdapter = new PagerAdapter() {
        public Object instantiateItem(android.view.ViewGroup container,
                final int position) {
            View root = getLayoutInflater().inflate(R.layout.layout_textview,
                    null);

            LayoutParams param = new LayoutParams(-1, -1);
            // TextView textView = new TextView(mActivity);
            // container.addView(textView, param);

            container.addView(root, param);
            TextView textView = (TextView) root.findViewById(R.id.textview);
            textView.setTypeface(BaseApplication.instance.getTypeface());
            String text = ciList.get(position % ciList.size()).getText();
            if (!TextUtils.isEmpty(ciList.get(position % ciList.size())
                    .getAuthor())) {
                text = ciList.get(position % ciList.size()).getAuthor() + "\n"
                        + text;
            }
            textView.setText(text);
            textView.setMaxLines(5);
            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, CiActivity.class);
                    intent.putExtra("cilist", ciList);
                    intent.putExtra("cipai", cipai);
                    intent.putExtra("num", position % ciList.size());
                    startActivity(intent);
                }
            });

            return root;
        }

        public void destroyItem(android.view.ViewGroup container, int position,
                Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object) % ciList.size();
        }

        @Override
        public int getCount() {
            if (ciList == null) {
                return 0;
            }
            return Integer.MAX_VALUE;
        }
    };

}
