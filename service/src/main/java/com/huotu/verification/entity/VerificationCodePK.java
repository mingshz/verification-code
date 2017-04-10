/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification.entity;

import com.huotu.verification.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodePK implements Serializable {
    private static final long serialVersionUID = -6663341165809474810L;
    private String mobile;
    private int type;


    public VerificationCodePK(String mobile, VerificationType type) {
        this.mobile = mobile;
        this.type = type.id();
    }
}
