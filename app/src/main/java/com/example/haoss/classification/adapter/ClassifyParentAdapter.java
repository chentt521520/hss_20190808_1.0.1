package com.example.haoss.classification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.applibrary.entity.GoodClassify;
import com.example.haoss.R;

import java.util.List;

//分类父类适配器
public class ClassifyParentAdapter extends BaseAdapter {

    private Context context;
    private List<GoodClassify> list;    //数据
    private int chooseIndex;    //选中的下标,默认第一个

    public ClassifyParentAdapter(Context context, List<GoodClassify> list) {
        this.context = context;
        this.list = list;
    }

    public void refresh(List<GoodClassify> list) {
        this.list = list;
        notifyDataSetChanged();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_textview_a, null);
            info = new Info();
            info.item = view.findViewById(R.id.item_good_classfy);
            info.item_textviewa = view.findViewById(R.id.item_textviewa);
            info.view_indicator = view.findViewById(R.id.view_indicator);
            view.setTag(info);
        }
        info = (Info) view.getTag();
        info.item_textviewa.setText(list.get(position).getCate_name());
        if (chooseIndex == position) {
            info.item_textviewa.getPaint().setFakeBoldText(true);
            info.view_indicator.setVisibility(View.VISIBLE);
        } else {
            info.item_textviewa.getPaint().setFakeBoldText(false);
            info.view_indicator.setVisibility(View.GONE);
        }
        return view;
    }

    //设置选中的下标
    public void setChoose(int index) {
        chooseIndex = index;
    }

    class Info {
        RelativeLayout item;
        View view_indicator;
        TextView item_textviewa;
    }
}
