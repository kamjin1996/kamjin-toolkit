![](https://ftp.bmp.ovh/imgs/2019/12/9fdfb3fbd3e6225d.jpeg)


<br>
<div id = "englishRead"></div>

<br>

English | [中文文档](README.MD)
## illustrate:

 <br>
A toolkit that can be integrated in any java project, providing some framework supplements, extensions and easy-to-use packaging. The direction is to make project development easier and faster, and the code becomes concise, not limited to any code
<br>

### Function list:
<a href="#db.crypt">1. Database encryption and decryption</a><br>
<a href="#db.crypt">2. Wait for it</a><br>
<a href="#db.crypt">3...</a><br>


<h3><div id="db.crypt">Database encryption and decryption</div></h3>

<br>

Simple and fast data encryption and decryption plug-in, supports global database encryption -> inbound encryption & outbound decryption

<br>

#### Mainly have the following characteristics:

+ Lightweight, relying in the form of jar package, minimal intrusion

+ Simple and flexible to use, just add @CryptField annotation on the data model field to be encrypted or before the method parameter of mapperInterface, then the target can be encrypted and decrypted

+ The configuration method is simple and flexible:
  +mybatisplus-springboot supports starter mode, no configuration is required, mybatis uses javaconfig,

+ Wide application, applicable to any JAVA project that uses Mybatis or MybatisPlus as the DAO layer

#### Dependence:

The pom.xml file can be written in the following coordinates:

+ Based on mybatis-plus-boot-stater:

       <dependency>
           <groupId>com.github.kamjin1996</groupId>
           <artifactId>kamjin-db-crypt-mybatis-plus-boot-starter</artifactId>
           <version>${last.version}</version>
       </dependency>

+ Based on mybatis-spring:

       <dependency>
           <groupId>com.github.kamjin1996</groupId>
           <artifactId>kamjin-db-crypt-mybatis</artifactId>
           <version>${last.version}</version>
       </dependency>

#### Configuration method:

**1. Starter method (recommended): **

<br>
    No configuration is required, the following parameters are taken by default

       kamjin:
           dbcrypt:
            secretkey: 123456789012345678901234
            enable: true
            primary-key-name: id
            aes: AES192


###### Configuration Interpretation:
| Item | Interpretation | Default Value |
| ---- | ---- | ---- |
| kamjin.dbcrypt.secretkey | The key required for aes encryption. Note that the length needs to match the selected aes standard. For example, the key length corresponding to aes192 is 24 bits | 123456789012345678901234 |
| kamjin.dbcrypt.enable | Whether to enable the plug-in, true or false | true |
| kamjin.dbcrypt.primary-key-name | The field name corresponding to the unified primary key ID of the database table in pojo, generally "id", which can be customized | id |
| kamjin.dbcrypt.aes | aes encryption standard enumeration value Optional values ​​AES128, AES192, AES256 | AES192 |


**Two, non-starter method: **

<br>
      1). application.yaml:
          Same as the starter configuration, PS: The key of the parameter can be customized in this way, as long as it can be parsed by spring.

      2). New configuration class:

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

Note: If you need to use javaBeanConfig to configure the MybatisPlusCryptInterceptor object and other related configurations, you also need @bean to create MybatisInterceptorInspectByAppRefreshedListener into the spring container

Configured
<br>
If the project is longer, use mybatis.xml to register the plug-in:

        <plugins>
            <plugin interceptor="com.kamjin.toolkit.db.crypt.mybatisplus.interceptor.MybatisPlusCryptInterceptor"/>
        </plugins>

Other bean definitions can be maintained by spring in xml


#### How to use:

Class field annotation @CryptField, also supports annotation @CryptField before the mapper enters parameters

Common usage, annotations on pojo fields

+ Use on the field

      @CryptField private String password;

+ Use mapper to enter the parameters

      User selectByMobile(@CryptField @Param("mobile") String mobile);

_Attach the quick start demo address:
https://github.com/kamjin1996/cryptdemo

### Special Note:
1. The example class (used for conditional splicing query) generated by mybatis in reverse is not supported. Even if the example class is used as the input parameter of the conditional query, if the example is useful in the project, and you want to encrypt the fields in the condition, then you can change to the form of the entity class to perform the conditional query

2. Because the field encryption will change the original value and store in the database, the fuzzy query will be invalid. The keywords of the fuzzy query will be completely different strings after encryption. Even if the unencrypted database strings are very similar to the keywords, they cannot be queried. of

3. If the project uses Mybatis, please use MybatisCryptInterceptor, if it is MybatisPlus, please use MybatisPlusCryptInterceptor, MybatisPlusCryptInterceptor is also compatible with Mybatis, but two plug-ins cannot be registered at the same time

## finally:

-The code is maintained in my spare time, and we will consider supporting more functions and usage in the future

-The performance of the current project has been slightly optimized, and there are not many logs, so you can use it with confidence

-Welcome to put forward better opinions and help improve
