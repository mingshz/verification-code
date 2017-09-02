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
 * 验证类型
 *
 * @author CJ
 */
public interface VerificationType {
    /**
     * @return 每一种不同的类型都应当提供一个识别服
     */
    int id();

    /**
     * @return 过期秒数
     */
    default int expireSeconds() {
        return 5 * 60;
    }

    /**
     * @return 多少秒内不可重发
     */
    default int protectSeconds() {
        return 60;
    }

    /**
     * @return 是否允许多个验证码同时有效
     * @since 1.3
     */
    default boolean allowMultiple() {
        return false;
    }

    /**
     * @param code 随机码
     * @return 即将发送给手机的文本内容
     */
    String message(String code);

    /**
     * @return 随机码长度
     */
    default int codeLength() {
        return 4;
    }
}
