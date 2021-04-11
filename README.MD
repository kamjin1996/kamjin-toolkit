![](https://ftp.bmp.ovh/imgs/2019/12/9fdfb3fbd3e6225d.jpeg)

## <a href="#englishRead">Click here to read</a>

## 说明：

简单快捷的数据加解密插件，支持全局数据库加密 -> 入库加密 & 出库解密

<br>

### 主要有以下特点：

+ 轻量级，采用jar包的形式依赖，侵入极小

+ 使用简单灵活，只需在需加密的数据模型类字段上、或mapperInterface的方法参数前，加@CryptField 注解，便可对目标加解密

+ 配置方式简单，只需配置密钥和加密方式即可

+ 适用广，适用任何采用了mybatis作为DAO层的java项目

### 依赖：

已上传至maven官方中央仓库，在pom.xml文件写入以下坐标即可:

      <dependency>
          <groupId>com.github.kamjin1996</groupId>
          <artifactId>kamjin-toolkit</artifactId>
          <version>2.0</version>
      </dependency>

### 配置方式:

1. spring配置文件形式：

   _PS：关于参数的key，可以取任意参数，只要确保能被spring解析到即可。

   下面是例子：

        dbcrypt.enable=true #加密插件是否启用
        dbcrypt.secretkey=123456789012345678901234 #加密密钥（注意值长度和AES加密方式要匹配才可以）

2. 新建配置类:

        @Configuration
        @Data
        public class MybatisConfig {

            @Value("${dbcrypt.secretkey}")
            private String secretkey;

            @Value("${dbcrypt.enable}")
            private boolean enable;

            @Bean
            public CryptInterceptor cryptInterceptor() {
                return new CryptInterceptor();
            }

            @Bean
            public Dbcrypt dbcrypt() {
                return new Dbcrypt(AesEnum.AES192, getSecretkey(), isEnable());
            }
        }

配置完毕，注意配置的AES方式，上例中为192标准，请改为自己需要的标准，并且secretKey长度要匹配。

#### 注意:

假如是比较老的项目，插件的注册也可以在mybatis.xml中完成，如下：

        <plugins>
            <plugin interceptor="com.github.kamjin1996.mybatis.intercept.crypt.CryptInterceptor"/>
        </plugins>

其他bean（Dbcrypt类），请在applicationContext.xml中定义，并配置AES加密标准、密钥和是否启用即可。

### 使用方式:

在需要加密的数据模型字段上注解@CryptField，也支持在mapper入参前注解@CryptField； 需要注意的是，暂不支持在数据模型的父类注解，入参是子类的形式。

+ 字段上使用

      @CryptField private String password;

+ mapper入参使用

      User selectByMobile(@CryptField @Param("mobile") String mobile);

_附上快速开始的demo地址：
https://github.com/kamjin1996/cryptdemo

## 特别说明：

需要注意的是： 
1.此插件并不支持mybatis逆向生成的example类（用于条件拼接查询）。 即使example类作为条件查询的入参，如果项目中有用到example的地方，又希望加密条件中的字段，那么改为实体类的形式进行条件查询即可
2.因为字段加密，一切模糊查询都将失效（模糊查询的关键词加密后是完全不同的字符串，即使未加密的数据库字符串与关键词很相似），请知晓

## 最后：

共同探讨的朋友联系qq1569558447，163邮箱：kamjin1996@163.com


<br>
<br>
<div id = "englishRead"></div>

<br>

## He explains:

Simple, fast, support global database encryption - > warehousing encryption & outbound decryption

### It has the following characteristics:

+ Lightweight, jar package dependent, minimal intrusion

+ It is simple and flexible to use. You can encrypt and decrypt the target only by adding @ cryptfield annotation on the
  data model field to be encrypted or before the method parameter of mapperinterface

+ The configuration mode is simple, just configure the key and encryption mode

+ It is widely applicable to any Java project adopting mybatis as Dao layer

### Dependency:

It has been uploaded to the official central warehouse of maven, and the following coordinates can be written in pom.xml
file:

      <dependency>
          <groupId>com.github.kamjin1996</groupId>
          <artifactId>kamjin-toolkit</artifactId>
          <version>2.0</version>
      </dependency>

### Configuration mode:

1. Form of spring configuration file:

   _PS: as for the key of the parameter, you can take any parameter as long as it can be parsed by spring.

Here is an example:

      dbcrypt. Enable = true #whether the encryption plug-in is enabled

      dbcrypt.secretkey=123456789012345678901234 #encryption key (note that the value length and AES encryption method must match)

2. Create a new configuration class:

        @Configuration
        @Data
        public class MybatisConfig {

            @Value("${dbcrypt.secretkey}")
            private String secretkey;

            @Value("${dbcrypt.enable}")
            private boolean enable;

            @Bean
            public CryptInterceptor cryptInterceptor() {
                return new CryptInterceptor();
            }

            @Bean
            public Dbcrypt dbcrypt() {
                return new Dbcrypt(AesEnum.AES192, getSecretkey(), isEnable());
            }
        }

After configuration, pay attention to the AES mode of configuration. In the above example, it is 192 standard. Please
change it to the standard you need, and the length of the secret key should match.

#### Attention:

If it is an older project, the plug-in registration can also be completed in mybatis.xml, as follows:

      <plugins>
         <plugin interceptor="com.github.kamjin1996.mybatis.intercept.crypt.CryptInterceptor"/>
      </plugins>

Other beans (dbcrypt class), please define in applicationcontext.xml, and configure AES encryption standard, key and
whether to enable.

### Usage:

Annotate @ cryptfield on the data model field to be encrypted, and also support to annotate @ cryptfield before the
mapper enters the parameter;

It should be noted that the parent class annotation in the data model is not supported at present, and the input
parameter is in the form of a child class.

+ Use on field

      @CryptField private String password;

+ Mapper input

      User selectByMobile(@CryptField @Param("mobile") String mobile);

_Attach the quick start demo address:

https://github.com/kamjin1996/cryptdemo

## Finally

It should be noted that this plug-in does not support mybatis reverse generated example class (used for conditional
splicing query).

Even if the example class is used as the input parameter of the condition query, if you want to encrypt the fields in
the condition when you use the example class in the project, you can query the condition in the form of entity class
instead

If you have any questions about this plug-in or want to discuss it together, please contact the author qq1569558447,163
email: kamjin1996@163.com

