package com.example.administrator.reboot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class LineGridView extends GridView {
    public LineGridView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public LineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        View localView1 = getChildAt(0);
        int column = getWidth() / localView1.getWidth();
        int childCount = getChildCount();
        Paint localPaint;
        localPaint = new Paint();
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setColor(getContext().getResources().getColor(R.color.black));

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getContext().getResources().getColor(R.color.black));


        for (int i = 0; i < childCount; i++)
        {
            View cellView = getChildAt(i);
            //画子view的底部横线  新增加的 _xx  这里为什么是-？？随便打的为什么？
            canvas.drawLine(cellView.getLeft(), cellView.getBottom() - 1, cellView.getRight(), cellView.getBottom() - 1, paint);

            //顶部线，坐标+1是为了画在cellView上
            canvas.drawLine(cellView.getLeft(), cellView.getTop() + 1, cellView.getRight(), cellView.getTop() + 1, paint);
            //左边线
            canvas.drawLine(cellView.getLeft() + 1, cellView.getTop(), cellView.getLeft() + 1, cellView.getBottom(), paint);
            if ((i + 1) % column == 0)      //最右边一列单元格画上右边线
            {
                canvas.drawLine(cellView.getRight(), cellView.getTop() + 1, cellView.getRight(), cellView.getBottom() + 1, paint);

            }
            if ((i + column) >= childCount)  //最后column个单元格画上底边线
            {
                canvas.drawLine(cellView.getLeft(), cellView.getBottom() + 1, cellView.getRight(), cellView.getBottom() + 1, paint);
            }
            if (childCount % column != 0 && i == childCount - 1)   //如果最后一个单元格不在最右一列，单独为它画上右边线
            {
                canvas.drawLine(cellView.getRight() + 1, cellView.getTop() + 1, cellView.getRight() + 1, cellView.getBottom() + 1, paint);
            }


           /* if (i == 0) {//如果view
                //画子view的底部横线
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
            }*/
        }

    }
}
