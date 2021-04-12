![](https://ftp.bmp.ovh/imgs/2019/12/9fdfb3fbd3e6225d.jpeg)

## <a href="#englishRead">Click here to read</a>

## 说明：
 
 <br>
可以在任何java项目集成的工具包，提供一些框架的补充、拓展和易用性封装，方向是让项目开发变得更加容易，更快速，代码变得简洁，不局限于任何代码
<br>

### 功能目录：
   <a href="#db.crypt">1.数据库加解密</a><br>
   <a href="#db.crypt">2.虚位以待</a><br>
   <a href="#db.crypt">3...</a><br>


<h3><div id="db.crypt">数据库加解密</div></h3>

<br>

 简单快捷的数据加解密插件，支持全局数据库加密 -> 入库加密 & 出库解密

<br>

#### 主要有以下特点：

+ 轻量级，采用jar包的形式依赖，侵入极小

+ 使用简单灵活，只需在需加密的数据模型类字段上、或mapperInterface的方法参数前，加@CryptField 注解，便可对目标加解密

+ 配置方式简单灵活，只需配置密钥、加密方式与主键名称，可自定义配置项a.b.c形式

+ 适用广，适用任何采用了Mybatis或MybatisPlus作为DAO层的JAVA项目

#### 依赖：

pom.xml文件写入以下坐标即可:

      <dependency>
          <groupId>com.github.kamjin1996</groupId>
          <artifactId>toolkit-db-crypt-mybatis-plus</artifactId>
          <version>${last.version}</version>
      </dependency>

#### 配置方式:

1. springboot的yam配置方式：
   _PS：关于参数的key，可以取任意参数，只要确保能被spring解析到即可。
   
       dbcrypt:
         secretkey: 123456789012345678901234 #加密的secretkey，需要注意长度，如果是AES加密（默认），需要和AES加密匹配才行
         enable: true #当前插件的加解密是否启用
         primaryKeyName: id #pojo中作为id字段的名称，非数据库名称

2. 新建配置类:

        @Configuration
        @Data
        public class MybatisCryptConfig {

          @Value("${dbcrypt.secretkey}")
          private String secretkey;

          @Value("${dbcrypt.enable}")
          private boolean enable;

          @Value("${dbcrypt.primaryKeyName}")
          private String primaryKeyName;

          @Bean
          public MybatisPlusCryptInterceptor mybatisPlusCryptInterceptor() {
              return new MybatisPlusCryptInterceptor();
          }

          @Bean
          public DbcryptProperties dbcrypt() {
              return new DbcryptProperties(AesEnum.AES192, getSecretkey(), isEnable(), getPrimaryKeyName());
          }
      }


配置完毕，注意上例中加密方式为AES192标准，如有需要请改为自己需要的标准，并且secretKey长度要匹配。
<br>
项目如果较久远，采用mybatis.xml注册插件：

        <plugins>
            <plugin interceptor="com.kamjin.toolkit.db.crypt.mybatisplus.interceptor.MybatisPlusCryptInterceptor"/>
        </plugins>
其他bean定义在xml中由spring维护即可

#### 使用方式:

类字段注解@CryptField，也支持在mapper入参前注解@CryptField

常见用法，pojo的字段上注解

+ 字段上使用

      @CryptField private String password;

+ mapper入参使用

      User selectByMobile(@CryptField @Param("mobile") String mobile);

_附上快速开始的demo地址：
https://github.com/kamjin1996/cryptdemo

### 特别说明：
 1.不支持mybatis逆向生成的example类（用于条件拼接查询）。 即使example类作为条件查询的入参，如果项目中有用到example的地方，又希望加密条件中的字段，那么改为实体类的形式进行条件查询即可
 
 2.因为字段加密会改变原有值落库存储，一切模糊查询都将失效，模糊查询的关键词加密后是完全不同的字符串，即使未加密的数据库字符串与关键词很相似，也是无法查询的

 3.若项目使用Mybatis，请使用MybatisCryptInterceptor，如果是MybatisPlus，请使用MybatisPlusCryptInterceptor，MybatisPlusCryptInterceptor也兼容Mybatis，但不可同时注册两个插件

## 最后：

- 代码是个人闲暇之余来维护的，后续会考虑支持更多的功能、用法

- 当前项目性能上做了些许优化，日志也并不多，可放心使用

- 欢迎提出更好的意见，帮助完善，联系我：QQ邮箱1569558447@qq.com


<br>
<br>
<div id = "englishRead"></div>

<br>

Description:

A toolkit that can be integrated in any java project, providing some framework supplements, extensions and easy-to-use packaging. The direction is to make project development easier and faster, and the code becomes concise, not limited to any code
Function catalog:
1. Database encryption and decryption
2. Waiting
3...

Database encryption and decryption

Simple and fast data encryption and decryption plug-in, supports global database encryption -> inbound encryption & outbound decryption


Mainly have the following characteristics:
Lightweight, relying in the form of jar package, minimal intrusion

Simple and flexible to use, just add @CryptField annotation on the data model field to be encrypted or before the method parameter of mapperInterface, then the target can be encrypted and decrypted

The configuration method is simple and flexible. You only need to configure the key, encryption method and primary key name, and the configuration item a.b.c form can be customized

Widely applicable, applicable to any JAVA project that uses Mybatis or MybatisPlus as the DAO layer

rely:
The pom.xml file can be written in the following coordinates:

  <dependency>
      <groupId>com.github.kamjin1996</groupId>
      <artifactId>toolkit-db-crypt-mybatis-plus</artifactId>
      <version>${last.version}</version>
  </dependency>
Configuration method:
Springboot's yam configuration method: _PS: Regarding the parameter key, you can take any parameter, as long as it can be parsed by spring.

dbcrypt:
  secretkey: 123456789012345678901234 #Encrypted secretkey, need to pay attention to the length, if it is AES encryption (default), it needs to match the AES encryption
  enable: true #Whether the encryption and decryption of the current plug-in is enabled
  primaryKeyName: id #pojo as the name of the id field, not the database name
New configuration class:

 @Configuration
 @Data
 public class MybatisCryptConfig {

   @Value("${dbcrypt.secretkey}")
   private String secretkey;

   @Value("${dbcrypt.enable}")
   private boolean enable;

   @Value("${dbcrypt.primaryKeyName}")
   private String primaryKeyName;

   @Bean
   public MybatisPlusCryptInterceptor mybatisPlusCryptInterceptor() {
       return new MybatisPlusCryptInterceptor();
   }

   @Bean
   public DbcryptProperties dbcrypt() {
       return new DbcryptProperties(AesEnum.AES192, getSecretkey(), isEnable(), getPrimaryKeyName());
   }
}

After the configuration is completed, note that the encryption method in the above example is the AES192 standard, if necessary, please change to the standard you need, and the secretKey length must match.
If the project is longer, use mybatis.xml to register the plug-in:

    <plugins>
        <plugin interceptor="com.kamjin.toolkit.db.crypt.mybatisplus.interceptor.MybatisPlusCryptInterceptor"/>
    </plugins>
Other bean definitions can be maintained by spring in xml

How to use:
Class field annotation @CryptField, also supports annotation @CryptField before the mapper enters parameters

Common usage, annotations on pojo fields

Use on the field

@CryptField private String password;
Use mapper as a parameter

User selectByMobile(@CryptField @Param("mobile") String mobile);
_Attach the quick start demo address: https://github.com/kamjin1996/cryptdemo

Special Note:
1. The example class (used for conditional splicing query) generated by mybatis in reverse is not supported. Even if the example class is used as the input parameter of the conditional query, if the example is useful in the project, and you want to encrypt the fields in the condition, then you can change to the form of the entity class to perform the conditional query

2. Because the field encryption will change the original value and store it in the database, all fuzzy queries will become invalid. The keywords of the fuzzy query will be completely different strings after encryption, even if the unencrypted database strings are very similar to the keywords, it will not be possible. Query

3. If the project uses Mybatis, please use MybatisCryptInterceptor, if it is MybatisPlus, please use MybatisPlusCryptInterceptor, MybatisPlusCryptInterceptor is also compatible with Mybatis, but two plug-ins cannot be registered at the same time

At last:
The project is maintained in my spare time, and we will consider supporting more functions and usage in the future.

The performance of the current project has been slightly optimized, and there are not many logs, so you can use it with confidence

Welcome to put forward better opinions and help improve, contact me: QQ mailbox 1569558447@qq.com  
