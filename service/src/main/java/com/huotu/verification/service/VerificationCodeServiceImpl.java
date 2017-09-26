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
import com.huotu.verification.VerificationType;
import com.huotu.verification.repository.VerificationCodeMultipleRepository;
import com.huotu.verification.repository.VerificationCodeRepository;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.NoticeService;
import me.jiangcai.lib.notice.exception.BadToException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author CJ
 */
@Service
public class VerificationCodeServiceImpl extends AbstractVerificationCodeService {

    private final String serverUrl;
    private final String account;
    private final String password;
    private final String noticeSupplier;
    @Autowired
    private NoticeService noticeService;

    @Autowired
    public VerificationCodeServiceImpl(Environment environment, VerificationCodeRepository verificationCodeRepository
            , VerificationCodeMultipleRepository verificationCodeMultipleRepository) {
        super(verificationCodeRepository, verificationCodeMultipleRepository);
        serverUrl = environment.getProperty("com.huotu.sms.cl.serverUrl"
                , "https://sms.253.com/msg/send");
        account = environment.getProperty("com.huotu.sms.cl.account");
        password = environment.getProperty("com.huotu.sms.cl.password");
        noticeSupplier = environment.getProperty("com.huotu.notice.supplier");
        if ((StringUtils.isEmpty(account) || StringUtils.isEmpty(password))
                && !environment.acceptsProfiles("test")) {
            if (StringUtils.isEmpty(noticeSupplier))
                throw new IllegalStateException("com.huotu.sms.cl.account and com.huotu.sms.cl.password or com.huotu.notice.supplier is required.");
        }
    }

    /**
     * @param mobiles 手机号码，多个号码使用","分割
     * @param content 短信内容
     */
    private void batchSend(String mobiles, Content content) throws IOException
            , URISyntaxException {
        if (!StringUtils.isEmpty(noticeSupplier)) {
            try {
                noticeService.send(noticeSupplier, () -> mobiles, content);
                return;
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("请确保将供应商" + noticeSupplier + "放置classpath", e);
            } catch (BadToException ex){
                throw new FrequentlySendException("短时间内不可以重复发送。",ex);
            }
        }
        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultConnectionConfig(ConnectionConfig.custom().build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(30000)
                        .setSocketTimeout(30000)
                        .setConnectionRequestTimeout(30000)
                        .build())
                .build()) {
            URIBuilder builder = new URIBuilder(serverUrl);
            builder.setParameters(
                    new BasicNameValuePair("un", account)
                    , new BasicNameValuePair("pw", password)
                    , new BasicNameValuePair("phone", mobiles)
                    , new BasicNameValuePair("rd", String.valueOf(true))
                    , new BasicNameValuePair("msg", content.asText())
            );

            HttpGet method = new HttpGet(builder.build());

            String text = client.execute(method, new BasicResponseHandler());
            int code = Integer.parseInt(text.split("\n")[0].split(",")[1]);
            if (code != 0)
                throw new IOException("sent failed, code:" + code);
        }
    }

    @Override
    protected void send(String to, Content content) throws IOException {
        try {
            batchSend(to, content);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected String generateCode(String mobile, VerificationType type) {
        return RandomStringUtils.randomNumeric(type.codeLength());
    }
}
