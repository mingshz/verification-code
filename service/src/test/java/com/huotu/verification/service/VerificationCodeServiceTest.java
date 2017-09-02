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
import com.huotu.verification.TestConfig;
import com.huotu.verification.VerificationCodeConfig;
import com.huotu.verification.VerificationType;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {VerificationCodeConfig.class, TestConfig.class})
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class VerificationCodeServiceTest {

    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private TestConfig testConfig;

    @Test
    public void g13() throws IOException, InterruptedException {
        VerificationType type = new VerificationType() {
            @Override
            public int id() {
                return 1;
            }

            @Override
            public boolean allowMultiple() {
                return true;
            }

            @Override
            public String message(String code) {
                return code;
            }

            @Override
            public int expireSeconds() {
                return 10;
            }

            @Override
            public int protectSeconds() {
                return 2;
            }
        };

        String mobile = RandomStringUtils.randomNumeric(11);
        String code1 = RandomStringUtils.randomNumeric(4);
        String code2 = RandomStringUtils.randomNumeric(4);

        testConfig.getReceived().clear();
        testConfig.setNextCode(code1);
        verificationCodeService.sendCode(mobile, type);

        // 这个时候应该收到了某个信息
        assertThat(testConfig.getReceived())
                .as("应当发送过")
                .contains(mobile);
        //再度发送 应当失败
        try {
            verificationCodeService.sendCode(mobile, type);
            throw new AssertionError("短时间内无法重复发送的");
        } catch (IllegalStateException ignored) {
        }

        //等待
        Thread.sleep(3000);

        testConfig.getReceived().clear();
        testConfig.setNextCode(code2);
        verificationCodeService.sendCode(mobile, type);
        assertThat(testConfig.getReceived())
                .as("应当发送过")
                .contains(mobile);

        // 现在执行验证
        try {
            verificationCodeService.verify(mobile, "ERROR", type);
            throw new AssertionError("错误的验证码是不可行的");
        } catch (IllegalVerificationCodeException ignored) {
        }

        verificationCodeService.verify(mobile, code1, type);
        verificationCodeService.verify(mobile, code2, type);
        // 再等待超时
        Thread.sleep(11000);
        try {
            verificationCodeService.verify(mobile, code1, type);
            throw new AssertionError("超时的验证码是不可行的");
        } catch (IllegalVerificationCodeException ignored) {

        }
    }

    @Test
    public void go() throws IOException, InterruptedException {
        testConfig.setNextCode(null);
        VerificationType type = new VerificationType() {
            @Override
            public int id() {
                return 0;
            }

            @Override
            public String message(String code) {
                return code;
            }

            @Override
            public int expireSeconds() {
                return 4;
            }

            @Override
            public int protectSeconds() {
                return 2;
            }
        };

        String mobile = RandomStringUtils.randomNumeric(11);

        testConfig.getReceived().clear();
        verificationCodeService.sendCode(mobile, type);

        // 这个时候应该收到了某个信息
        assertThat(testConfig.getReceived())
                .as("应当发送过")
                .contains(mobile);
        //再度发送 应当失败
        try {
            verificationCodeService.sendCode(mobile, type);
            throw new AssertionError("短时间内无法重复发送的");
        } catch (IllegalStateException ignored) {
        }

        //等待
        Thread.sleep(3000);


        testConfig.getReceived().clear();
        verificationCodeService.sendCode(mobile, type);
        assertThat(testConfig.getReceived())
                .as("应当发送过")
                .contains(mobile);

        // 现在执行验证
        try {
            verificationCodeService.verify(mobile, "ERROR", type);
            throw new AssertionError("错误的验证码是不可行的");
        } catch (IllegalVerificationCodeException ignored) {
        }

        verificationCodeService.verify(mobile, "1234", type);
        // 再等待超时
        Thread.sleep(5000);
        try {
            verificationCodeService.verify(mobile, "1234", type);
            throw new AssertionError("超时的验证码是不可行的");
        } catch (IllegalVerificationCodeException ignored) {

        }

    }

}