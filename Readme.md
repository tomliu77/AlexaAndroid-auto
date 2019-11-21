# 亚马逊 Alexa 安卓示例

*官方示例请看 https://github.com/willblaschko/AlexaAndroid *

## 修改的内容
1.由跳转到三方浏览器授权修改为跳转到webview(这里修改了login-with-amazon-sdk.jar)

2.授权时自动填写账号密码,隐式授权

3.远程依赖的gradle替换为本地依赖

## 使用方法
1.注册alexa账号并且创建一个Product,并生成Android key(此过程需要APK签名文件的MD5和SHA-256)

2.在目录\app\src\main\assets\下创建api_key.txt文件,复制Android/Kindle 的API key到api_key.txt

3.在目录\app\src\main\java\com\willblaschko\android\alexavoicelibrary\global\Constants.java中替换PRODUCT_ID为您的产品ID

4.清单文件AlexaAndroid-master\app\src\main\AndroidManifest.xml中AuthorizationActivity一项 ,data标签下的host修改为你自己的packagename
```java
   <!-- host should be our application package //-->
                <data
                    android:host="com.willblaschko.android.alexavoicelibrary"
                    android:scheme="amzn" />
```

5.项目根目录下创建\key\key.jks(必须是第一步的签名文件),并且在signingConfigs中配置好
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

6.在文件AlexaAndroid-master\libs\AlexaAndroid\src\main\java\com\willblaschko\android\alexa\AmazonAuthorizationActivity.java中,替换AMAZON_ACCOUNT和AMAZON_PSW为您的账号和密码

7.直接用Android studio跑起来
