package com.monsent.common.http;

/**
 * Created by Administrator on 2017/6/30.
 */

public interface OkHttpCallback {

    void onSuccess(HttpResult result);

    void onFailure(String msg);

}
