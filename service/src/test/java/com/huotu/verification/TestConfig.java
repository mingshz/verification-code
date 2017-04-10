/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification;

import com.huotu.verification.repository.VerificationCodeRepository;
import com.huotu.verification.service.AbstractVerificationCodeService;
import com.huotu.verification.service.VerificationCodeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CJ
 */
@Configuration
@Import({VerificationCodeConfig.class, DSConfig.class})
@ImportResource("classpath:/datasource_local.xml")
public class TestConfig {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Getter
    private final Set<String> received = new HashSet<>();

    @Bean
    @Primary
    public VerificationCodeService verificationCodeService() {
        return new AbstractVerificationCodeService(verificationCodeRepository) {
            @Override
            protected void send(String to, String content) throws IOException {
                received.add(to);
            }

            @Override
            protected String generateCode(String mobile, VerificationType type) {
                return "1234";
            }
        };
    }
}
