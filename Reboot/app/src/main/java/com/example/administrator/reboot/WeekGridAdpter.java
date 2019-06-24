/*
 *  autor：OrandNot
 *  email：orandnot@qq.com
 *  time: 2016 - 1 - 13
 *
 */
package com.example.administrator.reboot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeekGridAdpter extends BaseAdapter {

    private static final String TAG = "WeekGridAdpter";
    private Context context;
    private String[] weekStr = {"日","一","二","三","四","五","六"};
    private int selected = 0;
    private  AlarmsSetting alarmsSetting;
    private  int type;

    public WeekGridAdpter( Context context, AlarmsSetting alarmsSetting,int type) {
        this.context = context;
        this.alarmsSetting = alarmsSetting;
        this.type = type;
        if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN){
            this.selected =alarmsSetting.getInDays();
        }else{
            this.selected =alarmsSetting.getOutDays();
        }
    }

    public void updateSelected(int selected){
        this.selected = selected;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return weekStr.length;
    }

    @Override
    public Object getItem(int item) {
        // TODO Auto-generated method stub
        return weekStr[item];
    }

    @Override
    public long getItemId(int id) {
        // TODO Auto-generated method stub
        return id;
    }
    public void setSelection(int position) { //在activity中GridView的onItemClickListener中调用此方法，来设置选中位置
        lastPosition = position;
    }
    private int lastPosition;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_week_item, null);
            holder =new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder =new ViewHolder(convertView);
        }



        holder.text.setText(weekStr[position]);

        if (lastPosition == position){ //判断是否为选中项，选中项与非选中项设置不同的样式
            switch (position){  //选中状态下设置样式
                case 0:
                    //selected = selected + (int)(1 << position);
                    Log.d(TAG,"before setSelected true ,position is : " + position);
                    holder.text.setSelected(true);

                    break;
                case 1:
                    // selected = selected + (int)(1 << position);
                    Log.d(TAG,"before setSelected true ,position is : " + position );
                    holder.text.setSelected(true);

                    break;
                case 2:
                    Log.d(TAG,"before setSelected true ,position is : " + position );
                    holder.text.setSelected(true);

                    break;
                case 3:
                    Log.d(TAG,"before setSelected true ,position is : " + position );

                    holder.text.setSelected(true);

                    break;
                case 4:
                    Log.d(TAG,"before setSelected true ,position is : " + position );

                    holder.text.setSelected(true);

                    break;
                case 5:
                    Log.d(TAG,"before setSelected true ,position is : " + position );

                    holder.text.setSelected(true);

                    break;
                case 6:
                    Log.d(TAG,"before setSelected true ,position is : " + position );

                    holder.text.setSelected(true);

                    break;

            }
        }else {  //非选中状态下设置样式
            switch (position){
                case 0:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position  );
                    break;
                case 1:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position );
                    break;
                case 2:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position );
                    break;
                case 3:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position  );
                    break;
                case 4:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position  );
                    break;
                case 5:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position  );
                    break;
                case 6:
                    holder.text.setSelected(false);
                    Log.d(TAG,"after setSelected false ,position is : " + position  );
                    break;
            }
        }
        return convertView;
    }

    class ViewHolder{
        TextView text;
        public ViewHolder(View view){
            text = (TextView) view.findViewById(R.id.week_item_text);
        }
    }



}

