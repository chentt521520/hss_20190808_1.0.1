package com.example.applibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.applibrary.utils.DensityUtil;

public class CustomerCornerBg extends Drawable {

    private Context context;
    private Paint paint;
    private float height;

    public CustomerCornerBg(Context context, float height) {
        this.context = context;
        this.height = height;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        int width = DensityUtil.getScreenWidth(context);
        int h = DensityUtil.dip2px(context, height);
        RectF reft = new RectF(0, 0, width, h);
        canvas.drawRoundRect(reft, h / 2, h / 2, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
