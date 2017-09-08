/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.vefification.test;

import com.huotu.verification.VerificationCodeConfig;
import com.huotu.verification.VerificationType;
import com.huotu.verification.repository.VerificationCodeMultipleRepository;
import com.huotu.verification.repository.VerificationCodeRepository;
import com.huotu.verification.service.AbstractVerificationCodeService;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.lib.notice.Content;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

/**
 * @author CJ
 */
@Configuration
@Import(VerificationCodeConfig.class)
public class VerificationCodeTestConfig {

    private static final Log log = LogFactory.getLog(VerificationCodeTestConfig.class);

    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeMultipleRepository verificationCodeMultipleRepository;

    @Autowired
    public VerificationCodeTestConfig(VerificationCodeRepository verificationCodeRepository
            , VerificationCodeMultipleRepository verificationCodeMultipleRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationCodeMultipleRepository = verificationCodeMultipleRepository;
    }

    @Primary
    @Bean
    public VerificationCodeService verificationCodeService() {
        return new AbstractVerificationCodeService(verificationCodeRepository, verificationCodeMultipleRepository) {
            private final String code = "1234567890";

            @Override
            protected void send(String to, Content content) throws IOException {
                log.info("发送文本" + content + " 到" + to);
            }

            @Override
            protected String generateCode(String mobile, VerificationType type) {
                return code.substring(0, type.codeLength());
            }
        };
    }

}
