# 亚马逊 Alexa 安卓示例

*官方示例请看 https://github.com/willblaschko/AlexaAndroid *

## 修改的内容
1.由跳转到三方浏览器授权修改为跳转到webview(这里修改了login-with-amazon-sdk.jar)

2.授权时自动填写账号密码,隐式授权

3.远程依赖的gradle替换为本地依赖

## 使用方法

1.项目根目录下创建\key\key.jks,并且在signingConfigs中修改配置
```java
    signingConfigs {
        debug {
            storeFile file("../key/key.jks")
            storePassword "123456"
            keyAlias "xxx"
            keyPassword "123456"
        }
        release {
            storeFile file("../key/key.jks")
            storePassword "123456"
            keyAlias "xxx"
            keyPassword "123456"
        }
    }
```

2.注册alexa账号并且创建一个Product,并生成Android key(此过程需要第一步APK签名文件的MD5和SHA-256)
```java
 //获取MD5和SHA-256的命令如下,Keytool位于Java安装目录中,如(Java\jdk1.8.0_201\bin)
 keytool -list -v -alias <alias> -keystore <keystore.filename>
```

3.在主模块assets下创建api_key.txt文件,复制Android的API key(上一步生成的)到api_key.txt

4.在主模块Constants.java中替换PRODUCT_ID为您的产品ID

5.在AlexaAndroid模块AmazonAuthorizationActivity.java中,替换AMAZON_ACCOUNT和AMAZON_PSW为您的账号和密码

6.在主模块AndroidManifest.xml中AuthorizationActivity一项,data标签下的host修改为你自己的packagename
```java
   <!-- host should be our application package //-->
     <data
      android:host="com.willblaschko.android.alexavoicelibrary"
      android:scheme="amzn" />
```

7.主模块gradle中applicationId修改为alexa上注册的包名

8.用Android studio跑起来

## 注意事项
1.采用模拟点击的方式隐式授权,存在一定的风险(如Amazon网页内容发生变化会导致授权失败)

2.若长时间停在授权页面,请把act_showweb.xml中的遮罩布局隐藏掉,来观察当前的授权进度

3.核心代码在AmazonAuthorizationActivity.java中,如有需要请自行遮罩布局
