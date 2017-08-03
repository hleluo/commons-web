package com.monsent.common.util;

/**
 * Created by Administrator on 2017/6/27.
 */

public class StringUtils {

    public static boolean isEmpty(String source) {
        if (source != null && !"".equals(source)) {
            return false;
        }
        return true;
    }

    public static boolean isTrimEmpty(String source) {
        if (source != null && !"".equals(source.trim())) {
            return false;
        }
        return true;
    }
}
