package com.myth.cici.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.myth.poetrycommon.BaseApplication;
import com.myth.poetrycommon.utils.DisplayUtil;

public class ImageAdapter extends BaseAdapter
{

    private Context mContext;

    private int[] list = BaseApplication.instance.bgSmallimgList;

    public ImageAdapter(Context context)
    {
        mContext = context;
    }

    public int getCount()
    {
        return list == null ? 0 : list.length;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = new LinearLayout(mContext);
            ImageView imageView = new ImageView(mContext);
            ((LinearLayout) convertView).addView(imageView);
            imageView.setScaleType(ScaleType.FIT_XY);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 120),
                    DisplayUtil.dip2px(mContext, 120));
            imageView.setLayoutParams(layoutParams);
        }

        ((ImageView) ((LinearLayout) convertView).getChildAt(0)).setImageResource(BaseApplication.instance.bgSmallimgList[position]);
        return convertView;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

}
