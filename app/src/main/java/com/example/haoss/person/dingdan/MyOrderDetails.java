package com.example.haoss.person.dingdan;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.applibrary.dialog.MyDialogTwoButton;
import com.example.applibrary.dialog.interfac.DialogOnClick;
import com.example.applibrary.entity.OrderDetail;
import com.example.applibrary.entity.CartInfo;
import com.example.applibrary.httpUtils.OnHttpCallback;
import com.example.haoss.base.AppLibLication;
import com.example.applibrary.base.Netconfig;
import com.example.applibrary.utils.ImageUtils;
import com.example.applibrary.utils.IntentUtils;
import com.example.haoss.R;
import com.example.haoss.base.BaseActivity;
import com.example.haoss.goods.details.GoodsDetailsActivity;
import com.example.haoss.indexpage.tourdiy.GrouponDetailActivity;
import com.example.haoss.manager.ApiManager;
import com.example.haoss.pay.GoodsPayActivity;
import com.example.haoss.person.aftersale.AfterSaleActivity;

import java.util.HashMap;
import java.util.Map;

//订单详情
public class MyOrderDetails extends BaseActivity {

    private TextView orderStatus;  //交易状态
    private TextView expressDetail, userName, userPhone, userAddress; //物流提示、收货人、收货人电话、收货人地址

    private TextView goodPrice, express, coupon, icon, orderTotalPrice;
    private TextView orderNumber, dealNumber, createTime, payTime, sendTime, numberCopy;
    private TextView orderDelete, orderService, orderAgain;

    private LinearLayout order_item_container; //订单号
    private String orderId; //订单号
    private OrderDetail orderDetailsInfo;  //订单信息
    private int status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleContentView(R.layout.activity_my_order_details);

        orderId = getIntent().getStringExtra("orderId");
        init();
    }

    private void init() {
        this.getTitleView().setTitleText(getResources().getString(R.string.order_detail));

        order_item_container = findViewById(R.id.ui_order_item_container);

        //快递，收件人信息
        orderStatus = findViewById(R.id.ui_order_status);
        expressDetail = findViewById(R.id.ui_order_express_detail);
        userName = findViewById(R.id.ui_order_user_name);
        userPhone = findViewById(R.id.ui_order_user_phone);
        userAddress = findViewById(R.id.ui_order_user_address);

        //订单价格信息
        goodPrice = findViewById(R.id.ui_order_good_price);
        express = findViewById(R.id.ui_order_total_express);
        coupon = findViewById(R.id.ui_order_coupon);
        icon = findViewById(R.id.ui_order_icon);
        orderTotalPrice = findViewById(R.id.ui_order_total_price);

        //订单编号等信息
        orderNumber = findViewById(R.id.ui_order_number);
        dealNumber = findViewById(R.id.ui_order_deal_number);
        createTime = findViewById(R.id.ui_order_create_time);
        payTime = findViewById(R.id.ui_order_pay_time);
        sendTime = findViewById(R.id.ui_order_send_time);
        numberCopy = findViewById(R.id.ui_order_number_copy);

        //底部按钮
        orderDelete = findViewById(R.id.ui_order_delete);
        orderService = findViewById(R.id.ui_order_service);
        orderAgain = findViewById(R.id.ui_order_again);

        //点击事件
        findViewById(R.id.ui_order_express_info).setOnClickListener(onClickListener); //物流栏
        orderDelete.setOnClickListener(onClickListener);
        orderService.setOnClickListener(onClickListener);
        orderAgain.setOnClickListener(onClickListener);
        numberCopy.setOnClickListener(onClickListener);

        getOrderDetail();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_order_delete:   //删除商品
                    if (status == 0) {//取消订单
                        handleOrder(Netconfig.orderCancel, orderId, 3);
                    } else if (status == 3) {//删除订单
                        handleOrder(Netconfig.orderDelete, orderId, 2);
                    }
                    break;
                case R.id.ui_order_again:   //再次购买
                    if (status == 0) {//付款
                        toPayOrder();
                    } else if (status == 1) {//催单
                        toast(getResources().getString(R.string.attention_success));
                    } else if (status == 2) {//确认收货
                        confirmRecevie(orderId);
                    } else if (status == 3) {//评价
                        toPrise();
                    }
                    break;
                case R.id.ui_order_service:  //操作栏申请售后
                    if (status == 0) {//取消订单
                        handleOrder(Netconfig.orderCancel, orderId, 3);
                    } else if (status == 1 || status == 2) {//查看物流
                        express();
                    } else {//申请售后
                        afterOrder();
                    }
                    break;
                case R.id.ui_order_express_info:    //物流栏
                    express();
                    break;
                case R.id.ui_order_number_copy:    //复制单号
                    String on = numberCopy.getText().toString();
                    copyText(on);
                    break;
            }
        }
    };

    private void confirmRecevie(final String orderId) {

        MyDialogTwoButton myDialogTwoButton = new MyDialogTwoButton(MyOrderDetails.this, getResources().getString(R.string.corfirm_to_receive), new DialogOnClick() {
            @Override
            public void operate() {
                handleOrder(Netconfig.orderConfirmReceipt, orderId, 1);
            }

            @Override
            public void cancel() {
            }
        });
        myDialogTwoButton.show();

    }


    private void express() {
        Intent intent = new Intent(MyOrderDetails.this, ExpressActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

    private void toPrise() {
        String image = orderDetailsInfo.getCartInfo().get(0).getProductInfo().getAttrInfo().getImage();
        String unique = orderDetailsInfo.getCartInfo().get(0).getUnique();
        IntentUtils.startIntent(MyOrderDetails.this, EstimatePublishActivity.class, "goodsImage", image, "unique", unique);
    }

    private void toPayOrder() {
        Intent intent = new Intent(MyOrderDetails.this, GoodsPayActivity.class);
        intent.putExtra("orderId", orderDetailsInfo.getOrder_id());
        intent.putExtra("payType", orderDetailsInfo.getPay_type());
        intent.putExtra("price", orderDetailsInfo.getPay_price());
        startActivityForResult(intent, IntentUtils.intentActivityRequestCode);
    }

    private void handleOrder(String url, String order_id, int flag) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", AppLibLication.getInstance().getToken());
        if (flag == 3) {
            map.put("order_id", order_id);
        } else {
            map.put("uni", order_id);

        }

        //3取消订单，2删除订单
        ApiManager.getResultStatus(url, map, new OnHttpCallback<String>() {
            @Override
            public void success(String result) {
                finish();
            }

            @Override
            public void error(int code, String msg) {
                toast(code, msg);
            }
        });
    }

    //获取订单详情请求
    private void getOrderDetail() {

        if (orderId == null)
            orderId = "";
        String url = Netconfig.orderDetails;
        Map<String, Object> map = new HashMap<>();
        map.put("uni", orderId);
        map.put("token", AppLibLication.getInstance().getToken());

        ApiManager.getOrderDetail(url, map, new OnHttpCallback<OrderDetail>() {
            @Override
            public void success(OrderDetail result) {
                orderDetailsInfo = result;
                setControlData();
                setGoodList();
            }

            @Override
            public void error(int code, String msg) {
                toast(code, msg);
            }
        });
    }

    //设置控件数据
    private void setControlData() {
        status = orderDetailsInfo.getStatu().getType();
        orderStatus.setText(orderDetailsInfo.getStatu().getTitle());
        //用户
        userName.setText(orderDetailsInfo.getReal_name());
        userPhone.setText(orderDetailsInfo.getUser_phone());
        userAddress.setText(orderDetailsInfo.getUser_address());
        //价格
        goodPrice.setText(String.format(getResources().getString(R.string.price_unit), orderDetailsInfo.getTotal_price()));
        express.setText(String.format(getResources().getString(R.string.price_unit), orderDetailsInfo.getTotal_postage()));
        coupon.setText(String.format(getResources().getString(R.string.price_discount_unit), orderDetailsInfo.getCoupon_price()));
//        icon.setText(String.format(getResources().getString(R.string.price_discount_unit),orderDetailsInfo.getGain_integral()));
        orderTotalPrice.setText(String.format(getResources().getString(R.string.price_unit), orderDetailsInfo.getPay_price()));
        //订单
        orderNumber.setText(orderId);
        dealNumber.setText("");
        createTime.setText(orderDetailsInfo.getAdd_time());
        payTime.setText(orderDetailsInfo.getPay_time());

        if (status == 0 || status == 1) {
            findViewById(R.id.ui_order_express_info).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ui_order_express_info).setVisibility(View.VISIBLE);
            expressDetail.setText(TextUtils.isEmpty(orderDetailsInfo.getStatu().getMsg()) ? "" : orderDetailsInfo.getStatu().getMsg());
        }
        setButton(null, null, null);
        setView();
    }

    private void setView() {
        switch (status) {
            case 0://0 待付款
                setButton(null, getResources().getString(R.string.order_cancel), getResources().getString(R.string.pay_now));
                findViewById(R.id.ui_order_send_time_view).setVisibility(View.GONE);
                break;
            case 1://1 待发货
                setButton(null, null, getResources().getString(R.string.attention_send));
                findViewById(R.id.ui_order_send_time_view).setVisibility(View.GONE);
                break;
            case 2://2 待收货；
                setButton(null, getResources().getString(R.string.look_express), getResources().getString(R.string.confirm_receive));
                findViewById(R.id.ui_order_send_time_view).setVisibility(View.VISIBLE);
                break;
            case 3://3：待评价；
                setButton(getResources().getString(R.string.order_delete), getResources().getString(R.string.apply_after_sales), getResources().getString(R.string.order_prise));
                findViewById(R.id.ui_order_send_time_view).setVisibility(View.VISIBLE);
                break;
            case 4://4：已完成;
                setButton(getResources().getString(R.string.order_delete), getResources().getString(R.string.apply_after_sales), getResources().getString(R.string.buy_again));
                findViewById(R.id.ui_order_send_time_view).setVisibility(View.VISIBLE);
                break;
        }
    }

    //设置按钮数据
    private void setButton(String delete, String aftersale, String buy) {
        if (delete == null) {//删除
            orderDelete.setVisibility(View.INVISIBLE);
        } else {
            orderDelete.setVisibility(View.VISIBLE);
            orderDelete.setText(delete);
        }
        if (aftersale == null) { //售后
            orderService.setVisibility(View.INVISIBLE);
        } else {
            orderService.setVisibility(View.VISIBLE);
            orderService.setText(aftersale);
        }
        if (buy == null) {   //买
            orderAgain.setVisibility(View.INVISIBLE);
        } else {
            orderAgain.setVisibility(View.VISIBLE);
            orderAgain.setText(buy);
        }

    }

    //复制文本
    private void copyText(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);
        toast(getResources().getString(R.string.copy_success));
    }

    //订单售后
    private void afterOrder() {
        IntentUtils.startIntent(this, AfterSaleActivity.class);
    }

    //再次购买
    private void buyAgain(int goodsId) {
        IntentUtils.startIntent(goodsId, MyOrderDetails.this, GoodsDetailsActivity.class);
    }

    private void setGoodList() {
        order_item_container.removeAllViews();
        for (CartInfo cartInfo : orderDetailsInfo.getCartInfo()) {
            View view = LayoutInflater.from(MyOrderDetails.this).inflate(R.layout.item_list_order_detail, null);

            ImageView image = view.findViewById(R.id.ui_order_item_good_image);
            TextView name = view.findViewById(R.id.ui_order_item_good_name);
            TextView money = view.findViewById(R.id.ui_order_item_good_price);
            TextView suk = view.findViewById(R.id.ui_order_item_good_suk);
            TextView number = view.findViewById(R.id.ui_order_item_good_count);
            TextView service = view.findViewById(R.id.ui_order_item_good_service);

            name.setText(cartInfo.getProductInfo().getStore_name());
            if (cartInfo.getProductInfo().getAttrInfo() == null) {
                suk.setText("");
                money.setText(String.format(getResources().getString(R.string.price_unit), cartInfo.getProductInfo().getPrice()));
                ImageUtils.imageLoad(MyOrderDetails.this, cartInfo.getProductInfo().getImage(), image, 0, 0);
            } else {
                suk.setText(String.format(getResources().getString(R.string.good_suk), cartInfo.getProductInfo().getAttrInfo().getSuk()));
                money.setText(String.format(getResources().getString(R.string.price_unit), cartInfo.getProductInfo().getAttrInfo().getPrice()));
                ImageUtils.imageLoad(MyOrderDetails.this, cartInfo.getProductInfo().getAttrInfo().getImage(), image, 0, 0);
            }
            number.setText(String.format(getResources().getString(R.string.count), cartInfo.getCart_num()));


            if (status == 3 || status == 4) {
                service.setVisibility(View.VISIBLE);
            } else {
                service.setVisibility(View.GONE);
            }

            service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    afterOrder();
                }
            });
            final int product_id = cartInfo.getProduct_id();
            final int conbination_id = cartInfo.getCombination_id();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (conbination_id == 0) {
                        IntentUtils.startIntent(product_id, MyOrderDetails.this, GoodsDetailsActivity.class);
                    } else {
                        Intent intent = new Intent(MyOrderDetails.this, GrouponDetailActivity.class);
                        intent.putExtra("id", conbination_id);
                        startActivity(intent);
                    }

                }
            });
            order_item_container.addView(view);
        }
    }

}
