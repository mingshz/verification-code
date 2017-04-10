/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification.service;

import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.VerificationType;
import com.huotu.verification.entity.VerificationCode;
import com.huotu.verification.entity.VerificationCodePK;
import com.huotu.verification.repository.VerificationCodeRepository;

import java.io.IOException;
import java.util.Calendar;

/**
 * @author CJ
 */
public abstract class AbstractVerificationCodeService implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    public AbstractVerificationCodeService(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Override
    public void verify(String mobile, String code, VerificationType type) throws IllegalVerificationCodeException {
        VerificationCode verificationCode = verificationCodeRepository.findOne(new VerificationCodePK(mobile, type));
        if (verificationCode == null)
            throw new IllegalVerificationCodeException(type);
        if (!verificationCode.getCode().equals(code))
            throw new IllegalVerificationCodeException(type);
        // 过期了
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, -type.expireSeconds());
        if (instance.after(verificationCode.getSendTime()))
            throw new IllegalVerificationCodeException(type);
    }

    @Override
    public void sendCode(String mobile, VerificationType type) throws IOException {
        // 短时间内不允许 1 分钟?
        // 有效时间 10分钟?
        VerificationCode verificationCode = verificationCodeRepository.findOne(new VerificationCodePK(mobile, type));
        if (verificationCode != null) {
            final Calendar instance = Calendar.getInstance();
            instance.add(Calendar.SECOND, -type.protectSeconds());

            if (instance.before(verificationCode.getSendTime()))
                throw new IllegalStateException("短时间内不可以重复发送。");
        } else {
            verificationCode = new VerificationCode();
            verificationCode.setMobile(mobile);
            verificationCode.setType(type.id());
        }

        String code = generateCode(mobile, type);

        // 执行发送
        send(mobile, type.message(code));

        // 保存数据库
        verificationCode.setCode(code);
        verificationCode.setSendTime(Calendar.getInstance());
        verificationCodeRepository.save(verificationCode);
    }

    /**
     * 实际的发送文本
     *
     * @param to      接受手机号码
     * @param content 内容
     */
    protected abstract void send(String to, String content) throws IOException;

    /**
     * @param mobile 手机号码
     * @param type   类型
     * @return 生成随机码
     */
    protected abstract String generateCode(String mobile, VerificationType type);
}
