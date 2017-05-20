# 火图验证码通用库
该项目尽可能简化手机短信验证码的使用。

## 版本
* 1.0 原始版本
* 1.1 创蓝新版本

## 指南
### 导入该库
在pom.xml文件中允许使用火图私库

    <repository>
        <id>repo-huotu</id>
        <name>Hot Repository</name>
        <url>http://repo.51flashmall.com:8081/nexus/content/groups/public/</url>
    </repository>

并且载入该库:

		<dependency>
			<groupId>com.huotu.verification-code</groupId>
			<artifactId>vc-service</artifactId>
			<version>1.0</version>
		</dependency>

### 项目配置
#### 载入`VerificationCodeConfig`
#### JPA增配
需要将包`com.huotu.verification.entity`也作为JPA的实体包
### 运行环境配置
需要分别设置
* `com.huotu.sms.cl.account` 发送SMS的帐号
* `com.huotu.sms.cl.password` 发送SMS的密码

以上系统属性

## 单元测试
在pom.xml中加载测试库

        <dependency>
            <groupId>com.huotu.verification-code</groupId>
            <artifactId>vc-test</artifactId>
            <version>1.0</version>
        </dependency>

并且载入`VerificationCodeTestConfig`即可

## 1.2 版本新增功能
允许通过设置`com.huotu.notice.supplier`为Notice供应商类名进行短信发送。