/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification.service;

import com.huotu.verification.FrequentlySendException;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.Sender;
import com.huotu.verification.VerificationType;
import com.huotu.verification.entity.VerificationCode;
import com.huotu.verification.entity.VerificationCodeMultiple;
import com.huotu.verification.entity.VerificationCodePK;
import com.huotu.verification.repository.VerificationCodeMultipleRepository;
import com.huotu.verification.repository.VerificationCodeRepository;
import me.jiangcai.lib.notice.Content;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractVerificationCodeService implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeMultipleRepository verificationCodeMultipleRepository;

    @Autowired
    public AbstractVerificationCodeService(VerificationCodeRepository verificationCodeRepository
            , VerificationCodeMultipleRepository verificationCodeMultipleRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationCodeMultipleRepository = verificationCodeMultipleRepository;
    }

    @Override
    public void verify(String mobile, String code, VerificationType type) throws IllegalVerificationCodeException {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, -type.expireSeconds());

        if (type.allowMultiple()) {
            List<VerificationCodeMultiple> list = verificationCodeMultipleRepository.findByMobileAndType(mobile, type.id());
            if (list.isEmpty())
                throw new IllegalVerificationCodeException(type);
            // 过滤掉过期的，再过滤掉不匹配的，如果剩下还存在
            if (list.stream().filter(verificationCodeMultiple -> verificationCodeMultiple.getCode().equals(code))
                    .filter(verificationCodeMultiple -> instance.before(verificationCodeMultiple.getSendTime()))
                    .count() == 0
                    )
                throw new IllegalVerificationCodeException(type);
        } else {
            VerificationCode verificationCode = verificationCodeRepository.findOne(new VerificationCodePK(mobile, type));
            if (verificationCode == null)
                throw new IllegalVerificationCodeException(type);
            if (!verificationCode.getCode().equals(code))
                throw new IllegalVerificationCodeException(type);
            // 过期了

            if (instance.after(verificationCode.getSendTime()))
                throw new IllegalVerificationCodeException(type);
        }
    }

    @Override
    public void sendCode(Sender sender, String mobile, VerificationType type) throws IOException {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, -type.protectSeconds());
        // 短时间内不允许 1 分钟?
        // 有效时间 10分钟?
        if (type.allowMultiple()) {
            List<VerificationCodeMultiple> list = verificationCodeMultipleRepository.findByMobileAndType(mobile, type.id());
            if (!list.isEmpty()) {
                // 最近发的
                if (instance.before(list.stream().map(VerificationCodeMultiple::getSendTime)
                        .max(Calendar::compareTo)
                        .orElse(null))
                        )
                    throw new FrequentlySendException("短时间内不可以重复发送。");
            }
            // 添加一个
            VerificationCodeMultiple verificationCode = new VerificationCodeMultiple();
            verificationCode.setMobile(mobile);
            verificationCode.setType(type.id());

            String code = generateCode(mobile, type);

            // 执行发送
            send(sender, mobile, type.generateContent(code));

            // 保存数据库
            verificationCode.setCode(code);
            verificationCode.setSendTime(Calendar.getInstance());
            verificationCodeMultipleRepository.save(verificationCode);

        } else {
            VerificationCode verificationCode = verificationCodeRepository.findOne(new VerificationCodePK(mobile, type));
            if (verificationCode != null) {


                if (instance.before(verificationCode.getSendTime()))
                    throw new FrequentlySendException("短时间内不可以重复发送。");
            } else {
                verificationCode = new VerificationCode();
                verificationCode.setMobile(mobile);
                verificationCode.setType(type.id());
            }

            String code = generateCode(mobile, type);

            // 执行发送
            send(sender, mobile, type.generateContent(code));

            // 保存数据库
            verificationCode.setCode(code);
            verificationCode.setSendTime(Calendar.getInstance());
            verificationCodeRepository.save(verificationCode);
        }
    }

    @Override
    public void sendCode(String mobile, VerificationType type) throws IOException {
        sendCode(null,mobile,type);
    }

    /**
     * 实际的发送文本
     * @param sender
     * @param to      接受手机号码
     * @param content 内容
     */
    protected abstract void send(Sender sender, String to, Content content) throws IOException;

    /**
     * @param mobile 手机号码
     * @param type   类型
     * @return 生成随机码
     */
    protected abstract String generateCode(String mobile, VerificationType type);
}
