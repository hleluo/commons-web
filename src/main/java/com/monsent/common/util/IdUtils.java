package com.monsent.common.util;

import java.util.UUID;

/**
 * Created by lj on 2017/8/1.
 */

public class IdUtils {

    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args){
        System.out.println(uuid());
    }

}
