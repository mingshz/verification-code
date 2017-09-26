/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification;

/**
 * 验证码发送过于频繁
 *
 * @author CJ
 * @since 1.2
 */
public class FrequentlySendException extends IllegalStateException {

    public FrequentlySendException(String s) {
        super(s);
    }

    public FrequentlySendException(String message, Throwable cause) {
        super(message, cause);
    }
}
