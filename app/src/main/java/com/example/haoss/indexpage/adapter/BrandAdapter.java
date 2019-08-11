package com.example.haoss.indexpage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.applibrary.entity.BannerInfo;
import com.example.applibrary.utils.DensityUtil;
import com.example.haoss.R;

import java.util.List;

//品牌精品适配器
public class BrandAdapter extends BaseAdapter {
    private Context context;
    private List<BannerInfo> list;   //精品数据

    public BrandAdapter(Context context, List<BannerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void refresh(List<BannerInfo> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.grid_item_image, null);
            int width = (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 60)) / 3;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, width);
            view.setLayoutParams(params);
            info = new Info();
            info.image = view.findViewById(R.id.item_brand_image);
            view.setTag(info);
        }
        info = (Info) view.getTag();
        BannerInfo brandInfo = list.get(position);
        Glide.with(context).load(brandInfo.getImgUrl()).into(info.image);
        return view;
    }

    class Info {
        ImageView image;    //图片
    }
}
