package com.example.applibrary.dialog.citychoosedialog.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.applibrary.R;
import com.example.applibrary.dialog.citychoosedialog.entity.AreaInfo;

import java.util.List;

//地区适配器
public class AreaAdapter extends BaseAdapter {

    private Context context;
    private List<AreaInfo> list;
    private int index;  //选择的下标

    public AreaAdapter(Context context, List<AreaInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Info info;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_text, null);
            info = new Info();
            info.textView = view.findViewById(R.id.item_text_dialogitem);
            view.setTag(info);
        }
        info = (Info) view.getTag();
        info.textView.setText(list.get(position).getName());
        if (position == index)
            info.textView.setTextColor(Color.parseColor("#C22222"));
        else
            info.textView.setTextColor(Color.parseColor("#0f0f0f"));
        return view;
    }

    class Info {
        TextView textView;
    }

    //设置选择的下标
    public void setChoose(List<AreaInfo> list, int index) {
        this.list = list;
        this.index = index;
        notifyDataSetChanged();
    }
}
