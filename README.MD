![](https://ftp.bmp.ovh/imgs/2019/12/9fdfb3fbd3e6225d.jpeg)

[English](README_ENG.MD) | 中文文档

## 说明：

 <br>
可以在任何java项目集成的工具包，提供一些框架的补充、拓展和易用性封装，方向是让项目开发变得更加容易，更快速，代码变得简洁，不局限于任何代码
<br>

### 功能目录：
<a href="#db.crypt">1.数据库加解密</a><br>
<a href="#db.crypt">2...</a><br>

<br>

<h3><div id="db.crypt">数据库加解密</div></h3>


简单快捷的数据加解密插件，支持全局数据库加密 -> 入库加密 & 出库解密

#### 主要有以下特点：

+ 轻量级，采用jar包的形式依赖，侵入极小

+ 使用简单灵活，只需在需加密的数据模型类字段上、或mapperInterface的方法参数前，加@CryptField 注解，便可对目标加解密

+ 配置方式简单灵活：
  +mybatisplus-springboot支持starter方式，可以无需配置，mybatis使用javaconfig，

+ 适用广，适用任何采用了Mybatis或MybatisPlus作为DAO层的JAVA项目

#### 依赖：

pom.xml文件写入以下坐标即可:

+ 基于mybatis-plus-boot-stater:

       <dependency>
           <groupId>com.github.kamjin1996</groupId>
           <artifactId>kamjin-db-crypt-mybatis-plus-boot-starter</artifactId>
           <version>${last.version}</version>
       </dependency>

+ 基于mybatis-spring:

       <dependency>
           <groupId>com.github.kamjin1996</groupId>
           <artifactId>kamjin-db-crypt-mybatis</artifactId>
           <version>${last.version}</version>
       </dependency>

#### 配置方式:

<br>

**一、starter方式（推荐）：**<br>
可以不做配置，默认取下面的参数

       kamjin:
           dbcrypt:
            secretkey: 123456789012345678901234
            enable: true
            primary-key-name: id
            aes: AES192


###### 配置释义：
|  项   | 释义  | 默认值 |
|  ----  | ----  | ----  |
| kamjin.dbcrypt.secretkey  | aes加密所需的密钥，注意长度需要与所选aes标准匹配 比如aes192对应的密钥长度就是24位 | 123456789012345678901234 |
| kamjin.dbcrypt.enable  | 是否启用插件，true或false | true |
| kamjin.dbcrypt.primary-key-name  | pojo中数据库表主键ID统一所对应的字段名称，一般为"id"，可自定义 | id |
| kamjin.dbcrypt.aes  | aes加密标准枚举值 可选值AES128、AES192、AES256 | AES192 |

<br>

**二、非starter方式:**<br>
1). application.yaml：
同上starter的配置，PS:此方式关于参数的key可以自定义,只要确保能被spring解析到即可

      2). 新建配置类:

        @Configuration
        @Data
        public class KamjinDbCryptConfig {

          @Value("${kamjin.dbcrypt.secretkey}")
          private String secretkey;

          @Value("${kamjin.dbcrypt.enable}")
          private boolean enable;

          @Value("${kamjin.dbcrypt.primary-key-name}")
          private String primaryKeyName;

          @Value("${kamjin.dbcrypt.aes}")
          private AesEnum aes;

          @Bean
          public MybatisCryptInterceptor mybatisCryptInterceptor() {
              DbcryptProperties properties = new DbcryptProperties(aes, secretkey, enable, primaryKeyName);
              CryptExecutorFactory.registry(new DefaultCryptExecutor(new DefaultAESCodecFieldValueHandler(properties)));
              return new MybatisCryptInterceptor(properties);
          }
        }

注意：如果需要用javaBeanConfig配置MybatisPlusCryptInterceptor对象等相关配置，还需要@bean创建MybatisInterceptorInspectByAppRefreshedListener到spring容器中

配置完毕
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

2.因为字段加密会改变原有值落库存储，模糊查询都将失效，模糊查询的关键词加密后是完全不同的字符串，即使未加密的数据库字符串与关键词很相似，也是无法查询的

3.若项目使用Mybatis，请使用MybatisCryptInterceptor，如果是MybatisPlus，请使用MybatisPlusCryptInterceptor，MybatisPlusCryptInterceptor也兼容Mybatis，但不可同时注册两个插件

## 最后：

- 代码是个人闲暇之余来维护的，后续会考虑支持更多的功能、用法

- 当前项目性能上做了些许优化，日志也并不多，可放心使用

- 欢迎提出更好的意见，帮助完善

