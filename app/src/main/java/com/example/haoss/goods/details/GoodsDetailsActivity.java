package com.example.haoss.goods.details;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.applibrary.entity.GoodsDetailsInfo;
import com.example.applibrary.entity.ProductAttr;
import com.example.applibrary.entity.ReplyInfo;
import com.example.applibrary.entity.ShopCart;
import com.example.applibrary.entity.StoreInfo;
import com.example.applibrary.httpUtils.ErrorEnum;
import com.example.applibrary.httpUtils.OnHttpCallback;
import com.example.applibrary.utils.ImageUtils;
import com.example.applibrary.utils.StringUtils;
import com.example.applibrary.widget.CustomTitleView;
import com.example.haoss.base.AppLibLication;
import com.example.applibrary.base.ConfigHttpReqFields;
import com.example.applibrary.base.ConfigVariate;
import com.example.applibrary.base.Netconfig;
import com.example.applibrary.custom.viewfragment.FragmentDataInfo;
import com.example.applibrary.custom.viewfragment.FragmentView;
import com.example.applibrary.custom.viewfragment.OnclickFragmentView;
import com.example.applibrary.custom.webview.NoScrollWebView;
import com.example.applibrary.dialog.MyDialogTwoButton;
import com.example.applibrary.dialog.interfac.DialogOnClick;
import com.example.applibrary.dialog.sharedialog.ShareWeChar;
import com.example.applibrary.httpUtils.HttpHander;
import com.example.applibrary.utils.IntentUtils;
import com.example.applibrary.utils.TextViewUtils;
import com.example.haoss.R;
import com.example.haoss.base.BaseActivity;
import com.example.haoss.conversation.ServerOnlineActivity;
import com.example.haoss.goods.estimate.EstimateListActivity;
import com.example.haoss.manager.ApiManager;
import com.example.haoss.person.login.LoginActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//商品详情
public class GoodsDetailsActivity extends BaseActivity {

    int goodsId;    //商品id
    FragmentView carousel;  //轮播
    ArrayList<FragmentDataInfo> listBanner = new ArrayList<>(); //轮播
    NoScrollWebView webView;    //webview

    TextView goodsdetailsactivity_newmoney, goodsdetailsactivity_shopprice, goodsdetailsactivity_monthlysales; //新区间价格、商家优惠、月销量
    TextView goodsdetailsactivity_originalprice, goodsdetailsactivity_intro;    //元区间价格、商品简单说明(商品名称)
    TextView goodsdetailsactivity_shipmentsland, goodsdetailsactivity_newbag, goodsdetailsactivity_juan;    //发货地、新人礼包、领劵
    TextView goodsdetailsactivity_content, goodsdetailsactivity_exempt, goodsdetailsactivity_loss;  //净含量、免运费、说明
    TextView good_estimate_num, good_favorable_rate, user_name, estmate_content;   //评价数量、满意度
    ImageView userHead;
    RelativeLayout good_estimate_layout;   //评价列表

    RelativeLayout action_button_kefu, action_button_collect, action_button_car;  //客服、收藏、购物车
    TextView action_button_collect_text, action_button_car_number;    //收藏变更、购物车数量
    TextView action_button_add, action_button_pay; //加入购物车、购买
    boolean isCollect;  //是否收藏
    MyDialogTwoButton myDialogTwoButton;    //登录提示对话框
    DialogGoodsPayOrAddCar dialogGoodsPayOrAddCar;  //购买和加入购物车对话框
    ShareWeChar shareWeChar;    //分享对话框
    private AppLibLication application;
    private GoodsDetailsInfo detailsInfo;
    private int flag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleContentView(R.layout.layout_goodsdetailsactivity);
        application = AppLibLication.getInstance();
        init();
        initTitle();
    }

    private void initTitle() {
        CustomTitleView titleView = this.getTitleView();
        titleView.setTitleText("商品详情");
        titleView.setRightImage(R.drawable.goods_share);
        titleView.setRightImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login()) {
                    return;
                }
                if (shareWeChar == null) {
                    shareWeChar = new ShareWeChar(GoodsDetailsActivity.this, detailsInfo.getStoreInfo().getStore_name(),
                            detailsInfo.getStoreInfo().getImage(), detailsInfo.getStoreInfo().getStore_info(), detailsInfo.getDetails_url());
                } else {
                    shareWeChar.setUpData(detailsInfo.getStoreInfo().getStore_name(),
                            detailsInfo.getStoreInfo().getImage(), detailsInfo.getStoreInfo().getStore_info(), detailsInfo.getDetails_url());
                }
                shareWeChar.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carousel.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentUtils.intentActivityRequestCode && resultCode == RESULT_OK) {
            //从支付成功回调到此处，并调到订单列表，同时关闭自己
            IntentUtils.startIntent(0, this, IntentUtils.getIntentClass(this, ConfigVariate.packOrderForm));
            finish();
        }
    }

    //初始化
    public void init() {
        webView = findViewById(R.id.goodsdetailsactivity_webview);
        webView.initWebview(webView);

        carousel = findViewById(R.id.goodsdetailsactivity_carousel);
        goodsdetailsactivity_newmoney = findViewById(R.id.goodsdetailsactivity_newmoney);
        goodsdetailsactivity_shopprice = findViewById(R.id.goodsdetailsactivity_shopprice);
        goodsdetailsactivity_monthlysales = findViewById(R.id.goodsdetailsactivity_monthlysales);
        goodsdetailsactivity_originalprice = findViewById(R.id.goodsdetailsactivity_originalprice);
        goodsdetailsactivity_intro = findViewById(R.id.goodsdetailsactivity_intro);
        goodsdetailsactivity_shipmentsland = findViewById(R.id.goodsdetailsactivity_shipmentsland);
        goodsdetailsactivity_newbag = findViewById(R.id.goodsdetailsactivity_newbag);
        goodsdetailsactivity_juan = findViewById(R.id.goodsdetailsactivity_juan);
        goodsdetailsactivity_content = findViewById(R.id.goodsdetailsactivity_content);
        goodsdetailsactivity_exempt = findViewById(R.id.goodsdetailsactivity_exempt);
        goodsdetailsactivity_loss = findViewById(R.id.goodsdetailsactivity_loss);
        good_estimate_num = findViewById(R.id.good_estimate_num);
        good_favorable_rate = findViewById(R.id.good_favorable_rate);
        good_estimate_layout = findViewById(R.id.good_estimate_layout);
        estmate_content = findViewById(R.id.estmate_content);
        userHead = findViewById(R.id.user_head);
        user_name = findViewById(R.id.user_name);

        //下5操作按钮
        action_button_kefu = findViewById(R.id.action_button_kefu);
        action_button_collect = findViewById(R.id.action_button_collect);
        action_button_car = findViewById(R.id.action_button_car);
        action_button_add = findViewById(R.id.action_button_add);
        action_button_pay = findViewById(R.id.action_button_pay);

        action_button_collect.setEnabled(false);
        action_button_car.setEnabled(false);
        action_button_add.setEnabled(false);
        action_button_pay.setEnabled(false);

        //操作变更
        action_button_collect_text = findViewById(R.id.action_button_collect_text);
        action_button_car_number = findViewById(R.id.action_button_car_number);

        //监听
        goodsdetailsactivity_juan.setOnClickListener(onClickListener);
        goodsdetailsactivity_content.setOnClickListener(onClickListener);
        action_button_kefu.setOnClickListener(onClickListener);
        action_button_collect.setOnClickListener(onClickListener);
        action_button_car.setOnClickListener(onClickListener);
        action_button_add.setOnClickListener(onClickListener);
        action_button_pay.setOnClickListener(onClickListener);
        good_estimate_layout.setOnClickListener(onClickListener);

        goodsdetailsactivity_originalprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        goodsId = getIntent().getIntExtra(IntentUtils.intentActivityFlag, 0);
        flag = getIntent().getIntExtra("flag", 0);
        getGoodDetail();
    }

    private void getGoodDetail() {
        String url;
        if (flag == ConfigVariate.flagSalesIntent) {
            url = Netconfig.seckillShopDetails;
        } else {
            url = Netconfig.commodityDetails;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", goodsId);
        map.put("token", application.getToken());
        httpHander.okHttpMapPost(GoodsDetailsActivity.this, url, map, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新购物车数量
        getShopCarNumber();
    }

    public void getShopCarNumber() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", AppLibLication.getInstance().getToken());

        ApiManager.getShopCart(Netconfig.shoppingCarList, map, new OnHttpCallback<ShopCart>() {
            @Override
            public void success(ShopCart result) {
                result.getValid().size();
                if (!StringUtils.listIsEmpty(result.getValid())) {//有数据
                    action_button_car_number.setVisibility(View.VISIBLE);
                    action_button_car_number.setText(result.getValid().size() + "");
                } else {
                    action_button_car_number.setVisibility(View.GONE);
                }
            }

            @Override
            public void error(int code, String msg) {
                toast(code, msg);
            }
        });
    }

    HttpHander httpHander = new HttpHander() {
        @Override
        public void onSucceed(Message msg) {
            super.onSucceed(msg);

            try {
                Map<String, Object> map = new Gson().fromJson(msg.obj.toString(), HashMap.class);
                if (map == null) {
                    toast(ErrorEnum.ERROR_10003.getCode(), ErrorEnum.ERROR_10003.getMsg());
                } else {
                    if (map.containsKey("code")) {
                        double code = Double.parseDouble((map.get("code") + ""));
                        if (code == 200) {
                            Map<String, Object> ret = (Map<String, Object>) map.get("data");
                            analysisJson(ret);
                        } else {
                            toast((int) code, TextUtils.isEmpty(map.get("msg") + "") ? ErrorEnum.ERROR_10006.getMsg() : map.get("msg") + "");
                        }
                    } else {
                        toast(ErrorEnum.ERROR_10005.getCode(), ErrorEnum.ERROR_10005.getMsg());
                    }
                }
            } catch (Exception e) {
                toast(ErrorEnum.ERROR_10004.getCode(), e.getMessage());
            }
        }
    };


    //解析
    private void analysisJson(Map<String, Object> map) {
        if (map != null) {

            action_button_collect.setEnabled(true);
            action_button_car.setEnabled(true);
            action_button_add.setEnabled(true);
            action_button_pay.setEnabled(true);

            detailsInfo = new GoodsDetailsInfo();

            Map<String, Object> storeInfoMap = (Map<String, Object>) map.get("storeInfo");
            //storeInfo
            if (storeInfoMap != null) {
                StoreInfo storeInfo = new StoreInfo();

                Map<String, String> mapString = httpHander.getStringMap(storeInfoMap, "cate_id", "image", "store_info",
                        "store_name", "unit_name", "postage", "ot_price", "price", "vip_price", "priceName", "title");
                Map<String, Integer> mapInteger = httpHander.getIntegerMap(storeInfoMap, "browse", "ficti", "give_integral",
                        "sales", "stock", "sort", "id", "product_id", "is_seckill", "store_type");
                //轮播
                ArrayList<Object> listImage = httpHander.getList(storeInfoMap, "slider_image");
                ArrayList<Object> images = httpHander.getList(storeInfoMap, "images");

                storeInfo.setId(mapInteger.get("id"));
                /**
                 * 正常商品没有productId,只有id,此时id=product_id
                 * 特价商品有id和product_id，
                 */
                if (flag == ConfigVariate.flagSalesIntent) {
                    storeInfo.setProduct_id(mapInteger.get("id"));
                } else {
                    storeInfo.setProduct_id(mapInteger.get("product_id"));
                }
                storeInfo.setCate_id(mapString.get("cate_id"));
                storeInfo.setImage(mapString.get("image"));
                storeInfo.setStore_info(mapString.get("store_info"));
                storeInfo.setTitle(mapString.get("title"));
                storeInfo.setStore_name(mapString.get("store_name"));
                storeInfo.setUnit_name(mapString.get("unit_name"));
                storeInfo.setBrowse(mapInteger.get("browse"));
                storeInfo.setFicti(mapInteger.get("ficti"));
                storeInfo.setGive_integral(mapString.get("give_integral"));
                storeInfo.setSales(mapInteger.get("sales"));
                storeInfo.setStock(mapInteger.get("stock"));
                storeInfo.setPostage(mapString.get("postage"));
                storeInfo.setOt_price(mapString.get("ot_price"));
                storeInfo.setPrice(mapString.get("price"));
                storeInfo.setSlider_image(listImage);
                storeInfo.setImages(images);
                storeInfo.setUserCollect((Boolean) storeInfoMap.get("userCollect"));
                storeInfo.setStore_type(mapInteger.get("store_type"));


                listBanner.clear();
                if (storeInfo.getSlider_image() != null && storeInfo.getSlider_image().size() > 0) {
                    for (int i = 0; i < storeInfo.getSlider_image().size(); i++) {
                        String imageUrl = storeInfo.getSlider_image().get(i) + "";
                        FragmentDataInfo fragmentDataInfo = new FragmentDataInfo();
                        fragmentDataInfo.setImageUrl(imageUrl);
                        listBanner.add(fragmentDataInfo);
                    }
                } else if (storeInfo.getImages() != null && storeInfo.getImages().size() > 0) {
                    for (int i = 0; i < storeInfo.getImages().size(); i++) {
                        String imageUrl = storeInfo.getImages().get(i) + "";
                        FragmentDataInfo fragmentDataInfo = new FragmentDataInfo();
                        fragmentDataInfo.setImageUrl(imageUrl);
                        listBanner.add(fragmentDataInfo);
                    }
                }

                detailsInfo.setStoreInfo(storeInfo);
            }
            ArrayList<Object> productAttrList = (ArrayList<Object>) map.get("productAttr");
            //productAttr
            List<ProductAttr> AttrList = new ArrayList<>();
            if (productAttrList != null) {
                for (int i = 0; i < productAttrList.size(); i++) {
                    Map<String, Object> mapProductAttr = (Map<String, Object>) productAttrList.get(i);

                    int pid = (int) httpHander.getDouble(mapProductAttr, "product_id");
                    String name = httpHander.getString(mapProductAttr, "attr_name");
                    ArrayList list = httpHander.getList(mapProductAttr, "attr_values");
                    ProductAttr productAttr = new ProductAttr(pid, name, list);
                    AttrList.add(productAttr);
                }
            }
            detailsInfo.setProductAttr(AttrList);

            Map<String, Object> productValue = httpHander.getMap(map, "productValue");
            detailsInfo.setProductValue(productValue);

            Map<String, String> string = httpHander.getStringMap(map, "priceName", "details_url", "replyChance");
            Map<String, Integer> intNum = httpHander.getIntegerMap(map, "replyCount", "mer_id");
//            detailsInfo.setMer_id();
//            detailsInfo.setNotFreight();
            detailsInfo.setDetails_url(string.get("details_url"));
            detailsInfo.setPriceName(string.get("priceName"));
            detailsInfo.setReplyCount(intNum.get("replyCount"));
            detailsInfo.setReplyChance(string.get("replyChance"));

            Map<String, Object> reply = (Map<String, Object>) map.get("reply");
            if (reply != null) {
                ReplyInfo replayInfo = new ReplyInfo(httpHander.getString(reply, "nickname"), httpHander.getString(reply, "avatar"), httpHander.getString(reply, "comment"));
                detailsInfo.setReply(replayInfo);
            } else {
                detailsInfo.setReply(null);
            }
            setData();
        }

    }

    //设置数据
    private void setData() {
        setCarousel();
        webView.loadUrl(detailsInfo.getDetails_url());
        String priceName = TextUtils.isEmpty(detailsInfo.getPriceName()) ? detailsInfo.getStoreInfo().getPrice() : detailsInfo.getPriceName();
        goodsdetailsactivity_newmoney.setText("¥ " + priceName);
        goodsdetailsactivity_originalprice.setText("¥ " + detailsInfo.getStoreInfo().getOt_price());
        goodsdetailsactivity_exempt.setText("¥ " + detailsInfo.getStoreInfo().getPostage());
        goodsdetailsactivity_monthlysales.setText("月销 " + detailsInfo.getStoreInfo().getFicti());
        if (flag == ConfigVariate.flagSalesIntent) {
            goodsdetailsactivity_intro.setText(detailsInfo.getStoreInfo().getTitle());
        } else {
            goodsdetailsactivity_intro.setText(detailsInfo.getStoreInfo().getStore_name());
        }
        isCollect = !detailsInfo.getStoreInfo().isUserCollect();
        collectUpdata();
        if (detailsInfo.getReply() == null) {
            findViewById(R.id.no_estmate_item).setVisibility(View.VISIBLE);
            findViewById(R.id.estmate_item).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.no_estmate_item)).setText("暂无评价");
        } else {
            findViewById(R.id.no_estmate_item).setVisibility(View.GONE);
            findViewById(R.id.estmate_item).setVisibility(View.VISIBLE);
            good_estimate_num.setText("(" + detailsInfo.getReplyCount() + ")");
            good_favorable_rate.setText("满意度" + detailsInfo.getReplyChance() + "%");
            user_name.setText(detailsInfo.getReply().getNickname());
            ImageUtils.loadCirclePic(this, detailsInfo.getReply().getAvatar(), userHead);
            estmate_content.setText(detailsInfo.getReply().getComment());
        }
    }

    //设置轮播数据
    private void setCarousel() {
        carousel.addFragment(getSupportFragmentManager(), listBanner, 3000, new OnclickFragmentView() {
            @Override
            public void onItemclick(int id, String url) {
                //轮播图点击操作
//                tost("点击ID;" + id);
            }
        });
    }

    //点击监听
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.good_estimate_layout) {  //查看评论列表
                if (flag == ConfigVariate.flagSalesIntent) {
                    IntentUtils.startIntent(detailsInfo.getStoreInfo().getId(), GoodsDetailsActivity.this, EstimateListActivity.class);
                } else {
                    IntentUtils.startIntent(detailsInfo.getStoreInfo().getId(), GoodsDetailsActivity.this, EstimateListActivity.class);
                }
            }

            if (login())
                return;
            if (v.getId() == R.id.goodsdetailsactivity_content) {//净含量
                dialogPayAndCar();
            } else if (v.getId() == R.id.action_button_kefu) {//客服
                startActivity(new Intent(GoodsDetailsActivity.this, ServerOnlineActivity.class));
            } else if (v.getId() == R.id.action_button_collect) {//收藏
                addOrCancelCollect();
            } else if (v.getId() == R.id.action_button_car) {//购物车
                IntentUtils.startIntentRepeatedly(1, GoodsDetailsActivity.this,
                        IntentUtils.getIntent(GoodsDetailsActivity.this, ConfigVariate.packShopCat));
            } else if (v.getId() == R.id.action_button_add || //加入购物车
                    v.getId() == R.id.action_button_pay) {   //立即购买
                dialogPayAndCar();
            }
        }
    };

    //未登录则先登录
    private boolean login() {
        if (!application.isLogin()) {//未登录
            if (myDialogTwoButton == null)
                myDialogTwoButton = new MyDialogTwoButton(this, "您还未登录，是否立即登录？", new DialogOnClick() {
                    @Override
                    public void operate() {
                        //未登录首先登录
                        IntentUtils.startIntentForResult(0, GoodsDetailsActivity.this, LoginActivity.class);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            myDialogTwoButton.show();
            return true;
        }
        return false;
    }

    //添加或取消收藏
    private void addOrCancelCollect() {
        String url = "";
        if (!isCollect) //添加收藏
            url = Netconfig.addShoppingCollect;
        else    //取消收藏
            url = Netconfig.cancleShoppingCollect;
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConfigHttpReqFields.sendToken, application.getToken());
        map.put(ConfigHttpReqFields.sendProductId, goodsId);

        ApiManager.getResultStatus(url, map, new OnHttpCallback<String>() {
            @Override
            public void success(String result) {
                if (flag == ConfigVariate.flagSalesIntent) {
                    tost("特价商品不支持收藏");
                } else {
                    collectUpdata();
                }
            }

            @Override
            public void error(int code, String msg) {

            }
        });
    }

    //收藏图标修改
    private void collectUpdata() {
        if (isCollect) {  //取消收藏
            isCollect = false;
            action_button_collect_text.setText("收藏");
            action_button_collect_text.setTextColor(Color.parseColor("#0f0f0f"));
            TextViewUtils.setImage(GoodsDetailsActivity.this, action_button_collect_text, R.drawable.goods_collect, 2);
        } else {  //收藏好
            isCollect = true;
            action_button_collect_text.setText("已收藏");
            action_button_collect_text.setTextColor(Color.parseColor("#c22222"));
            TextViewUtils.setImage(GoodsDetailsActivity.this, action_button_collect_text, R.drawable.goods_collect_yes, 2);
        }
    }

    //立即购买和加入购物车对话框
    private void dialogPayAndCar() {
        if (detailsInfo != null) {
            if (dialogGoodsPayOrAddCar == null)
                dialogGoodsPayOrAddCar = new DialogGoodsPayOrAddCar(this, detailsInfo, flag);
            else
                dialogGoodsPayOrAddCar.setRefresh(detailsInfo);
            dialogGoodsPayOrAddCar.show();
        } else {
            tost("未获取到商品信息");
        }

    }
}
