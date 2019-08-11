package com.example.haoss.indexpage.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.applibrary.base.Netconfig;
import com.example.applibrary.custom.MyListView;
import com.example.applibrary.custom.viewfragment.FragmentDataInfo;
import com.example.applibrary.custom.viewfragment.FragmentView;
import com.example.applibrary.custom.viewfragment.OnclickFragmentView;
import com.example.applibrary.entity.BannerInfo;
import com.example.applibrary.entity.FestivalGift;
import com.example.applibrary.entity.Recommond;
import com.example.applibrary.httpUtils.OnHttpCallback;
import com.example.applibrary.utils.IntentUtils;
import com.example.haoss.R;
import com.example.haoss.base.BaseActivity;
import com.example.haoss.goods.details.GoodsDetailsActivity;
import com.example.haoss.goods.search.GoodsSearchActivity;
import com.example.haoss.indexpage.adapter.ListViewGiftCardAdapter;
import com.example.haoss.indexpage.adapter.FestivalGiftBagAdapter;
import com.example.haoss.manager.ApiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//年节礼包
public class FestivalGiftActivity extends BaseActivity {

    FragmentView carousel;  //轮播

    FestivalGiftBagAdapter giftPackageAdapter;    //生日汇-优选数据适配器
    ListViewGiftCardAdapter giftCardAdapter;    //生日汇-优选数据适配器

    ArrayList<FragmentDataInfo> listBanner = new ArrayList<>(); //轮播
    List<Recommond> giftPackageList = new ArrayList<>();  //生日汇-优选数据
    List<Recommond> giftCardList = new ArrayList<>(); //8选项
    private String title;
    private int id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleContentView(R.layout.activity_festival_gift);
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        id = bundle.getInt("id");
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carousel.cancel();
    }


    //初始化
    private void init() {
        this.getTitleView().setTitleText(title);

        carousel = findViewById(R.id.ui_bannar);
        findViewById(R.id.ui_grid_nav).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.ui_brand_recommad_title)).setText("节日礼包");

        MyListView giftCardListView = findViewById(R.id.festival_gift_card_list);

        RecyclerView gridBrandRecommad = findViewById(R.id.ui_grid_brand_recommad);

        //创建LinearLayoutManager 对象 这里使用 LinearLayoutManager 是线性布局的意思
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(0);
        //设置RecyclerView 布局
        gridBrandRecommad.setLayoutManager(layoutmanager);

        giftPackageAdapter = new FestivalGiftBagAdapter(this, giftPackageList);
        gridBrandRecommad.setAdapter(giftPackageAdapter);

        giftCardAdapter = new ListViewGiftCardAdapter(FestivalGiftActivity.this, giftCardList);
        giftCardListView.setAdapter(giftCardAdapter);

        findViewById(R.id.action_search_ss).setOnClickListener(onClickListener);
        giftCardListView.setOnItemClickListener(onGiftCardClickListener);
        giftPackageAdapter.setOnViewClickListener(onItemListener);

        gatData();
    }

    private void gatData() {
        String url = Netconfig.indexNav + Netconfig.headers;
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id + "");
        ApiManager.getFestivalGift(url, map, new OnHttpCallback<FestivalGift>() {
            @Override
            public void success(FestivalGift result) {
                List<BannerInfo> banner = result.getBanner();
                for (BannerInfo info : banner) {
                    FragmentDataInfo fragmentDataInfo = new FragmentDataInfo();
                    fragmentDataInfo.setId(info.getId());
                    fragmentDataInfo.setImageUrl(info.getImgUrl());
                    fragmentDataInfo.setOrder(info.getOrder());
                    listBanner.add(fragmentDataInfo);
                }
                setCarousel();
                giftPackageList = result.getNav();
                giftCardList = result.getRecommend();
                giftPackageAdapter.freshList(result.getNav());
                giftCardAdapter.refresh(result.getRecommend());
            }

            @Override
            public void error(int code, String msg) {
                toast(code, msg);
            }
        });
    }

    //设置轮播数据
    private void setCarousel() {
        carousel.addFragment(getSupportFragmentManager(), listBanner, 3000, new OnclickFragmentView() {
            @Override
            public void onItemclick(int id, String url) {
                //轮播图点击操作
            }
        });
    }


    //点击监听
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_title_goback:  //返回
                    finish();
                    break;
                case R.id.action_search_ss:  //搜索
                    IntentUtils.startIntent(FestivalGiftActivity.this, GoodsSearchActivity.class);
                    break;
            }
        }
    };

    FestivalGiftBagAdapter.OnItemClickListener onItemListener = new FestivalGiftBagAdapter.OnItemClickListener() {
        @Override
        public void onItemClickListener(int position) {
            IntentUtils.startIntent(giftPackageList.get(position).getId(), FestivalGiftActivity.this, GoodsDetailsActivity.class);
        }
    };
    ;

    //节日礼卡
    AdapterView.OnItemClickListener onGiftCardClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            IntentUtils.startIntent(giftCardList.get(position).getId(), FestivalGiftActivity.this, GoodsDetailsActivity.class);
        }
    };
}
