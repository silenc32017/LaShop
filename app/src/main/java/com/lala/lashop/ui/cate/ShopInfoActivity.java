package com.lala.lashop.ui.cate;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lala.lashop.Constant;
import com.lala.lashop.R;
import com.lala.lashop.app.App;
import com.lala.lashop.base.BaseActivity;
import com.lala.lashop.base.mvp.CreatePresenter;
import com.lala.lashop.http.ApiPath;
import com.lala.lashop.ui.cate.bean.ShopInfoBean;
import com.lala.lashop.ui.cate.presenter.ShopInfoPresenter;
import com.lala.lashop.ui.cate.view.ShopInfoView;
import com.lala.lashop.ui.home.bean.ShopsBean;
import com.lala.lashop.ui.shop.ConfirmIndentActivity;
import com.lala.lashop.ui.shop.bean.ConfirmBean;
import com.lala.lashop.ui.shop.bean.JieSuanBean;
import com.lala.lashop.utils.ArrayUtil;
import com.lala.lashop.utils.BannerImageLoader;
import com.lala.lashop.utils.RxBus;
import com.lala.lashop.widget.ColorGuisPopup;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商品详情
 * Created by JX on 2018/3/28.
 */

@CreatePresenter(ShopInfoPresenter.class)
public class ShopInfoActivity extends BaseActivity<ShopInfoView, ShopInfoPresenter> implements ShopInfoView {

    public static final String SHOP_ID = "shopid";

    @BindView(R.id.shopinfo_banner)
    Banner banner;
    @BindView(R.id.shopinfo_tv_title)
    TextView tvTitle;
    @BindView(R.id.shopinfo_tv_des)
    TextView tvDes;
    @BindView(R.id.shopinfo_tv_price)
    TextView tvPrice;
    @BindView(R.id.shopinfo_webView)
    WebView webView;
    @BindView(R.id.shopinfo_tv_select)
    TextView tvSelect;

    private String shopid; //商品id

    private String color; //颜色
    private String guis; //尺码
    private String count = "1"; //数量

    private ColorGuisPopup colorGuisPopup;
    private ShopInfoBean shopInfoBean;

    @Override
    public int setContentView() {
        return R.layout.shopinfo_activity;
    }

    @Override
    public void onCreate() {
        getToolbar().setTitle("商品详情");
        getToolbar().setRightImage(R.drawable.coll_icon_normal)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPresenter().collAdd();
                    }
                });
        shopid = getIntent().getStringExtra(SHOP_ID);

        getPresenter().getShopInfo();

        getPresenter().collCheck();

        getPresenter().addPath();
    }

    @Override
    public void setData(ShopInfoBean data) {
        this.shopInfoBean = data;

        initBanner(data.getShopPhoto());

        ShopsBean shopsBean = data.getShop();

        tvTitle.setText(shopsBean.getSp_title());
        tvDes.setText(shopsBean.getSp_discontent());
        tvPrice.setText("￥" + shopsBean.getSp_mprice());

        color = ArrayUtil.isEmpty(data.getColors()) ? "" : data.getColors().get(0).getName();
        guis = ArrayUtil.isEmpty(data.getGuis()) ? "" : data.getGuis().get(0).getName();

        setCurrentSelect();

        initImage(shopsBean);
    }

    @Override
    public void checkCollSuccess(Integer mess) {
        if (mess == 1) {
            getToolbar().setRightImage(R.drawable.coll_icon_select);
        } else {
            getToolbar().setRightImage(R.drawable.coll_icon_normal);
        }
    }

    @Override
    public void jieSuanSuccess(ConfirmBean data) {
        List<JieSuanBean> list = new ArrayList<>();
        ShopsBean shop = shopInfoBean.getShop();
        list.add(new JieSuanBean("",
                shop.getSp_id(),
                getCount(),
                shop.getSp_simg(),
                shop.getSp_title(),
                shop.getSp_mprice(),
                shop.getYunfei(),
                getColor(),
                getGui()));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConfirmIndentActivity.JIESUAN_LIST, (ArrayList<? extends Parcelable>) list);
        bundle.putParcelable(ConfirmIndentActivity.CONFIRM, data);
        bundle.putBoolean(ConfirmIndentActivity.ISBUYNOW, true);
        startActivity(ConfirmIndentActivity.class, bundle);
        RxBus.getInstance().post(Constant.CART);
    }

    private void setCurrentSelect() {
        tvSelect.setText("已选择：" + color + "，" + guis + "，" + count + "件");
    }

    /**
     * 轮播图
     */
    private void initBanner(List<ShopsBean> data) {
        List<String> images = new ArrayList<>();
        for (ShopsBean bean : data) {
            images.add(ApiPath.IMG_URL + bean.getSp_img());
        }
        banner.setImageLoader(new BannerImageLoader());
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(images);
        banner.start();
    }

    /**
     * 详情图片
     */
    private void initImage(ShopsBean data) {
        webView.getSettings().setJavaScriptEnabled(true);
        String htmlStr = data.getSp_body();
        htmlStr = htmlStr.replaceAll("&", "");
        htmlStr = htmlStr.replaceAll("<", "<");
        htmlStr = htmlStr.replaceAll(">", ">");
        htmlStr = htmlStr.replaceAll("\\n", "<br>");//换行
        htmlStr = htmlStr.replaceAll("<img", "<img width=\"100%\"");//图片不超出屏幕
        webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);
    }

    @Override
    public String getShopId() {
        return shopid;
    }

    @Override
    public String getUserId() {
        return App.getUser() != null ? App.getUser().getUser_id() : "";
    }

    @Override
    public String getUserIdId() {
        return App.getUser() != null ? App.getUser().getId() : "";
    }

    @Override
    public String getCount() {
        return count;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getGui() {
        return guis;
    }

    @Override
    public String getYunFei() {
        return shopInfoBean.getShop().getYunfei();
    }

    @OnClick({R.id.shopinfo_ll_select, R.id.shopinfo_tv_add, R.id.shopinfo_tv_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shopinfo_ll_select:
                if (shopInfoBean == null) return;
                if (colorGuisPopup == null) {
                    colorGuisPopup = new ColorGuisPopup(this, shopInfoBean);
                    colorGuisPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            color = colorGuisPopup.getColor();
                            guis = colorGuisPopup.getGuis();
                            count = colorGuisPopup.getCount();

                            setCurrentSelect();
                        }
                    });
                }
                colorGuisPopup.showPopupWindow(tvSelect);
                break;
            case R.id.shopinfo_tv_add:
                if (shopInfoBean == null) {
                    getPresenter().getShopInfo();
                } else {
                    if (!checkUser()) return;
                    getPresenter().addCart(false);
                }
                break;
            case R.id.shopinfo_tv_buy:
                if (!checkUser()) return;
                getPresenter().addCart(true);
                break;
        }
    }
}
