package com.tmp.thermaquil.customView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class CircularCardView extends CardView {
    public CircularCardView(@NonNull Context context) {
        super(context);
    }

    public CircularCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height) {
            setMeasuredDimension(width, width);
            setRadius(width/2);
        } else {
            setMeasuredDimension(height, height);
            setRadius(height/2);
        }
    }
}
