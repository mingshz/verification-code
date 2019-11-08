/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huotu.verification;

import com.huotu.verification.entity.VerificationCodeMultiple_;
import com.huotu.verification.entity.VerificationCode_;
import com.huotu.verification.repository.VerificationCodeMultipleRepository;
import com.huotu.verification.repository.VerificationCodeRepository;
import me.jiangcai.common.ss.SystemStringConfig;
import me.jiangcai.lib.notice.NoticeSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * @author CJ
 */
@Configuration
@Import({NoticeSpringConfig.class, SystemStringConfig.class})
@EnableJpaRepositories("com.huotu.verification.repository")
@EnableScheduling
@ComponentScan("com.huotu.verification.service")
public class VerificationCodeConfig {

    private static final Log log = LogFactory.getLog(VerificationCodeConfig.class);

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private VerificationCodeMultipleRepository verificationCodeMultipleRepository;

    /**
     * 把发送时间超过一天的记录清楚掉
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void autoDelete() {
        log.debug("auto delete vc.");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        verificationCodeRepository.findAll((root, query, cb)
                -> cb.lessThan(root.get(VerificationCode_.sendTime), calendar))
                .forEach(verificationCode -> verificationCodeRepository.delete(verificationCode));

        verificationCodeMultipleRepository.findAll((root, query, cb)
                -> cb.lessThan(root.get(VerificationCodeMultiple_.sendTime), calendar))
                .forEach(verificationCode -> verificationCodeMultipleRepository.delete(verificationCode));
    }

    @Bean
    public MessageSource vcMessageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("vc-messages");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        return resourceBundleMessageSource;
    }

}
