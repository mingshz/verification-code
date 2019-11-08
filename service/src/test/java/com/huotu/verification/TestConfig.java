/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification;

import com.huotu.verification.repository.VerificationCodeMultipleRepository;
import com.huotu.verification.repository.VerificationCodeRepository;
import com.huotu.verification.service.AbstractVerificationCodeService;
import com.huotu.verification.service.VerificationCodeService;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.NoticeSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

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
    @Autowired
    private VerificationCodeMultipleRepository verificationCodeMultipleRepository;
    @Getter
    private final Set<String> received = new HashSet<>();
    @Setter
    private String nextCode;

    @Bean
    @Primary
    public VerificationCodeService verificationCodeService() {
        return new AbstractVerificationCodeService(verificationCodeRepository, verificationCodeMultipleRepository) {
            @Override
            protected void send(NoticeSender sender, String to, Content content) throws IOException {
                received.add(to);
            }

            @Override
            protected String generateCode(String mobile, VerificationType type) {
                if (!StringUtils.isEmpty(nextCode))
                    return nextCode;
                return "1234";
            }
        };
    }
}
