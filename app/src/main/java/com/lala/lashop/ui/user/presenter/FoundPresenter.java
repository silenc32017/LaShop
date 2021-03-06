package com.lala.lashop.ui.user.presenter;

import android.text.TextUtils;

import com.lala.lashop.base.mvp.BasePresenter;
import com.lala.lashop.http.ApiSubscribers;
import com.lala.lashop.http.HttpResult;
import com.lala.lashop.http.exception.ApiException;
import com.lala.lashop.ui.user.bean.CodeBean;
import com.lala.lashop.ui.user.model.FoundModel;
import com.lala.lashop.ui.user.view.FoundView;

/**
 * Created by JX on 2018/4/18.
 */

public class FoundPresenter extends BasePresenter<FoundView> {

    private FoundModel mModel;

    public FoundPresenter() {
        mModel = new FoundModel();
    }

    public void smsSend() {
        String phone = getView().getPhone();

        if (TextUtils.isEmpty(phone)) {
            getView().toast("手机号不能为空");
            return;
        }

        getView().showLoadingDialog();

        mModel.sms_send(getView().getPhone())
                .compose(this.<HttpResult<CodeBean>>compose())
                .subscribe(new ApiSubscribers<HttpResult<CodeBean>>(getView()) {
                    @Override
                    public void onSuccess(HttpResult<CodeBean> stringHttpResult) {
                        getView().sendSuccess(stringHttpResult.getMess().getCode());
                    }

                    @Override
                    public void onError(ApiException e) {

                    }
                });

    }

    public void updatePwd() {
        String newPwd = getView().getNewPwd();
        String confirmPwd = getView().getConfirmPwd();

        if (TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)) {
            getView().toast("密码不能为空");
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            getView().toast("两次密码输入不一致");
            return;
        }

        getView().showLoadingDialog();

        mModel.user_updatePwd(getView().getPhone(), getView().getNewPwd())
                .compose(this.<HttpResult>compose())
                .subscribe(new ApiSubscribers<HttpResult>(getView()) {
                    @Override
                    public void onSuccess(HttpResult httpResult) {
                        getView().toast(httpResult.getMess().toString());
                        getView().updateSuccess();
                    }

                    @Override
                    public void onError(ApiException e) {

                    }
                });
    }
}
