package com.example.haoss.indexpage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.applibrary.base.Netconfig;
import com.example.applibrary.custom.CustomerScrollView;
import com.example.applibrary.custom.viewfragment.FragmentDataInfo;
import com.example.applibrary.custom.viewfragment.FragmentView;
import com.example.applibrary.custom.viewfragment.OnclickFragmentView;
import com.example.applibrary.entity.BannerInfo;
import com.example.applibrary.entity.MenuCategory;
import com.example.applibrary.entity.Nav;
import com.example.applibrary.entity.Recommond;
import com.example.applibrary.httpUtils.OnHttpCallback;
import com.example.applibrary.utils.IntentUtils;
import com.example.applibrary.utils.StringUtils;
import com.example.haoss.R;
import com.example.haoss.base.BaseActivity;
import com.example.haoss.goods.details.GoodsDetailsActivity;
import com.example.haoss.goods.goodslist.GoodsListActivity;
import com.example.haoss.goods.search.GoodsSearchActivity;
import com.example.haoss.indexpage.adapter.BrandRecommondAdapter;
import com.example.haoss.indexpage.adapter.CarouselAdapter;
import com.example.haoss.indexpage.adapter.GridFavorAdapter;
import com.example.haoss.indexpage.adapter.GridSortNavAdapter;
import com.example.haoss.indexpage.fragment.BannerFragment;
import com.example.haoss.manager.ApiManager;
import com.example.haoss.views.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BabyProductsActivity extends BaseActivity {

    FragmentView carousel;  //轮播
    ArrayList<FragmentDataInfo> listBanner; //轮播

    private List<Nav> listNav;//导航
    private List<Nav> listBrandRecommad;//品牌推荐
    /**
     * 猜你喜欢
     */
    private List<Recommond> listFavor;

    /**
     * 商品分类适配器
     */
    private GridSortNavAdapter gideNavAdapter;  //商品分类适配器
    /**
     * 品牌推荐适配器
     */
    private BrandRecommondAdapter brandRecommondAdapter;
    /**
     * 猜你喜欢适配器
     */
    private GridFavorAdapter gideFavorAdapter;  //礼包适配器
    private String title;
    private int id;
    private int page = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleContentView(R.layout.activity_baby_product);
        initData();
        init();
    }

    private void initData() {
        listBanner = new ArrayList<>();
        listNav = new ArrayList<>();
        listFavor = new ArrayList<>();
        listBrandRecommad = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        id = bundle.getInt("id");
    }

    //初始化
    private void init() {
        this.getTitleView().setTitleText(title);

        CustomerScrollView scrollView = findViewById(R.id.scroll_view);
        carousel = findViewById(R.id.ui_bannar);
        MyGridView gridNav = findViewById(R.id.ui_grid_nav);
        RecyclerView gridBrandRecommad = findViewById(R.id.ui_grid_brand_recommad);
        MyGridView gridFavor = findViewById(R.id.ui_grid_favor);

        gideNavAdapter = new GridSortNavAdapter(this, listNav);
        gridNav.setAdapter(gideNavAdapter);

        gideFavorAdapter = new GridFavorAdapter(this, listFavor);
        gridFavor.setAdapter(gideFavorAdapter);

        //创建LinearLayoutManager 对象 这里使用 LinearLayoutManager 是线性布局的意思
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(0);
        //设置RecyclerView 布局
        gridBrandRecommad.setLayoutManager(layoutmanager);

        brandRecommondAdapter = new BrandRecommondAdapter(this, listBrandRecommad);
        gridBrandRecommad.setAdapter(brandRecommondAdapter);

        findViewById(R.id.action_search_ss).setOnClickListener(onClickListener);
        gridNav.setOnItemClickListener(onNavClickListener);
        gridFavor.setOnItemClickListener(onFavorClickListener);
        brandRecommondAdapter.setOnViewClickListener(onItemListener);

        scrollView.setOnScrollListener(new CustomerScrollView.OnScrollListener() {
            @Override
            public void loadMore() {
                page++;
                getRecommond();
            }
        });

        getData();
        getRecommond();
    }

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id + "");
        String url = Netconfig.indexNav + Netconfig.headers;

        ApiManager.getMenuCategory(url, map, new OnHttpCallback<MenuCategory>() {
            @Override
            public void success(MenuCategory result) {
                List<BannerInfo> banner = result.getBanner();
                for (BannerInfo info : banner) {
                    FragmentDataInfo fragmentDataInfo = new FragmentDataInfo();
                    fragmentDataInfo.setId(info.getId());
                    fragmentDataInfo.setImageUrl(info.getImgUrl());
                    fragmentDataInfo.setOrder(info.getOrder());
                    listBanner.add(fragmentDataInfo);
                }

                listNav = result.getNav();
                listBrandRecommad = result.getBrand_recommendation();
                gideNavAdapter.refresh(result.getNav());
                brandRecommondAdapter.freshList(result.getBrand_recommendation());
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
    private void getRecommond() {
        String url = Netconfig.recommend;
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id + "");
        map.put("page", page);
        map.put("limit", 20);

        ApiManager.getFavorList(url, map, new OnHttpCallback<List<Recommond>>() {
            @Override
            public void success(List<Recommond> result) {
                if (!StringUtils.listIsEmpty(result)) {
                    listFavor.addAll(result);
                }
                gideFavorAdapter.refresh(listFavor);
            }

            @Override
            public void error(int code, String msg) {
                toast(code, msg);
            }
        });
    }

    //点击监听
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_search_ss:  //搜索
                    IntentUtils.startIntent(BabyProductsActivity.this, GoodsSearchActivity.class);
                    break;
            }
        }
    };

    //导航监听，进入对应的分类
    AdapterView.OnItemClickListener onNavClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(BabyProductsActivity.this, GoodsListActivity.class);
            intent.putExtra("searchType", listNav.get(position).getId());
            startActivity(intent);
        }
    };


    BrandRecommondAdapter.OnItemClickListener onItemListener = new BrandRecommondAdapter.OnItemClickListener() {
        @Override
        public void onItemClickListener(int position) {
            Intent intent = new Intent(BabyProductsActivity.this, GoodsListActivity.class);
            intent.putExtra("searchType", listBrandRecommad.get(position).getId());
            startActivity(intent);
        }
    };

    //猜你喜欢：具体商品-->点击进入该商品详情
    AdapterView.OnItemClickListener onFavorClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            IntentUtils.startIntent(listFavor.get(position).getId(), BabyProductsActivity.this, GoodsDetailsActivity.class);
        }
    };

}
