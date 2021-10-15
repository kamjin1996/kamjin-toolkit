package com.kamjin.toolkit.db.crypt.mybatis.builder;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import com.kamjin.toolkit.db.crypt.core.builder.AbstractMethodCryptMetadataBuilder;
import com.kamjin.toolkit.db.crypt.core.resolver.*;
import com.kamjin.toolkit.db.crypt.core.util.CryptHelper;
import org.apache.ibatis.annotations.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Mybatis的MethodCryptMetadata 的建造者
 *
 * @author kamjin1996
 */
public class MybatisMethodCryptMetadataBuilder extends AbstractMethodCryptMetadataBuilder {

    public MybatisMethodCryptMetadataBuilder(Method runningMethod) {
        super(runningMethod);
    }

    @Override
    protected MethodEncryptResolver buildEncryptResolver(Method m) {
        boolean methodNotExistOrNoParameter =
                Objects.isNull(m) || Objects.isNull(m.getParameters()) || m.getParameters().length == 0;
        if (methodNotExistOrNoParameter) {
            return EMPTY_ENCRYPT_RESOLVER;
        }

        List<MethodAnnotationEncryptParameter> willBeEncryptParameter = this.getCryptParams(m);

        // 单参数处理，不含@Param注解的参数
        boolean simpleParameter = m.getParameters().length == 1 && willBeEncryptParameter.size() == 1;
        if (simpleParameter) {
            return new SimpleMethodEncryptResolver(willBeEncryptParameter.get(0));
        }

        // 多参数处理,包含@Param注解后的参数以及复杂参数
        if (willBeEncryptParameter.size() > 0) {
            return new AnnotationMethodEncryptResolver(willBeEncryptParameter);
        }
        return EMPTY_ENCRYPT_RESOLVER;
    }

    @Override
    protected MethodDecryptResolver buildDecryptResolver(Method m) {
        if (Objects.isNull(m) || m.getReturnType() == Void.class) {
            return EMPTY_DECRYPT_RESOLVER;
        }
        return new SimpleMethodDecryptResolver();
    }

    public List<MethodAnnotationEncryptParameter> getCryptParams(Method m) {
        Parameter[] parameters = m.getParameters();
        if (Objects.isNull(parameters) || parameters.length == 0) {
            return new ArrayList<>();
        }

        List<MethodAnnotationEncryptParameter> resultList = new ArrayList<>();

        final Annotation[][] allParamsAnnotations = m.getParameterAnnotations();

        for (int i = 0; i < parameters.length; i++) {
            Param param = null;
            CryptField crypt = null;

            final Class<?> currentParamType = parameters[i].getType();
            String currentParamName = parameters[i].getName();
            final Annotation[] currentParamAnnotations = allParamsAnnotations[i];

            boolean currentParamIsExistAnnotations = currentParamAnnotations.length > 0;

            if (currentParamIsExistAnnotations) {
                // 遍历当前参数注解,有加密注解的，添加到paramList
                for (Annotation annotation : currentParamAnnotations) {
                    if (annotation instanceof CryptField) {
                        crypt = (CryptField) annotation;
                    }
                    if (annotation instanceof Param) {
                        param = (Param) annotation;
                    }
                }
            }

            // 有@Param注解时，name必须赋值给参数，且入参形式在mapperStament中表现为map的类型
            if (Objects.nonNull(param)) {
                currentParamName = param.value();
                // 此时为了防止走单参数处理，构造一个空参
                resultList.add(new MethodAnnotationEncryptParameter());
            }

            // 如果方法入参类型是map 或 IgnoreClass列表中的，则直接忽略
            if (currentParamType.isAssignableFrom(Map.class) || CryptHelper.inIgnoreClass(currentParamType)) {
                continue;
            }

            // string类型参数
            if (currentParamType.isAssignableFrom(String.class) && Objects.nonNull(crypt)) {
                resultList.addAll(this.dealString(currentParamType, currentParamName, crypt));
                break;
            }

            // 如果是实体类，获得实体类并判断是否有加密注解，有则视为需加密参数
            boolean isEntity =
                    !(currentParamType.isAssignableFrom(List.class) || currentParamType.isAssignableFrom(Collection.class)
                            || currentParamType.isAssignableFrom(Array.class));

            resultList.addAll(isEntity ? this.dealEntity(currentParamType, currentParamName, crypt)
                    : this.dealCollection(currentParamType, currentParamAnnotations, param));
        }
        return resultList;
    }

    private List<MethodAnnotationEncryptParameter> dealString(Class<?> type, String paramName, CryptField crypt) {
        List<MethodAnnotationEncryptParameter> result = new ArrayList<>();
        result.add(new MethodAnnotationEncryptParameter(paramName, crypt, type));
        return result;
    }

    private List<MethodAnnotationEncryptParameter> dealEntity(Class<?> type, String paramName, CryptField crypt) {
        List<MethodAnnotationEncryptParameter> result = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (Objects.nonNull(cryptField)) {
                result.add(new MethodAnnotationEncryptParameter(paramName, crypt, type));
                break;
            }
        }
        return result;
    }

    private List<MethodAnnotationEncryptParameter> dealCollection(Class<?> type, Annotation[] currentParamAnnotations,
                                                                  Param param) {
        List<MethodAnnotationEncryptParameter> result = new ArrayList<>();
        String name = null;
        if (type.isAssignableFrom(List.class)) {
            name = getParameterNameOrDefault(currentParamAnnotations, "list");
        } else if (type.isAssignableFrom(Collection.class)) {
            name = getParameterNameOrDefault(currentParamAnnotations, "collection");
        } else if (type.isArray()) {
            name = getParameterNameOrDefault(currentParamAnnotations, "array");
        }

        if (Objects.nonNull(name)) {
            // 集合入参比较特殊，需构造一个空的参数，防止集合类型无法映射到param
            result.add(new MethodAnnotationEncryptParameter());
            result.add(new MethodAnnotationEncryptParameter(Objects.nonNull(param) ? param.value() : name, null, type));
        }
        return result;
    }

    private String getParameterNameOrDefault(Annotation[] annotations, String defaultName) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                return ((Param) annotation).value();
            }
        }
        return defaultName;
    }
}
