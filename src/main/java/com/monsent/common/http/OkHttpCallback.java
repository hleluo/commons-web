package com.monsent.common.http;

/**
 * Created by luojia on 2018/5/17.
 */
public interface OkHttpCallback {

    void onSuccess(HttpResult result);

    void onFailure(String msg);
}
