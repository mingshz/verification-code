/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

/**
 * 验证码
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@IdClass(VerificationCodePK.class)
public class VerificationCode {

    /**
     * 接收者的手机号码
     */
    @Id
    @Column(length = 20)
    private String mobile;
    /**
     * 验证码类型
     */
    @Id
    private int type;
    /**
     * 验证码内容
     */
    @Column(length = 10)
    private String code;

    /**
     * 发送时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar sendTime;

}
