package com.myth.shishi.wiget;

import android.content.Context;
import android.util.AttributeSet;

import com.myth.poetrycommon.view.TouchEffectImageView;
import com.myth.shishi.R;

public class SelectImageView extends TouchEffectImageView
{



    public SelectImageView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public void setSelect(boolean select)
    {
        if (select)
        {
            setBackgroundResource(R.drawable.circle_select);
        }
        else
        {
            setBackgroundDrawable(null);
        }
    }

}
