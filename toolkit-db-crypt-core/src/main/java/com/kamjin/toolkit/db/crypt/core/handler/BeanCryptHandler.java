package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import com.kamjin.toolkit.db.crypt.core.util.BeanCloneUtil;
import com.kamjin.toolkit.db.crypt.core.util.CryptHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * * 处理 bean 实体的加解密
 *
 * @author kamjin1996
 */
public class BeanCryptHandler implements CryptHandler<Object> {

    private static final ConcurrentHashMap<Class<?>, List<CryptFiled>> CLASS_ENCRYPT_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, List<CryptFiled>> CLASS_DECRYPT_MAP = new ConcurrentHashMap<>();

    private static Object clone(Object bean) {
        Object result = null;
        try {
            result = BeanCloneUtil.clone(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Object encrypt(Object bean, CryptField cryptField) {
        if (Objects.isNull(bean)) {
            return null;
        }

        //对bean的所有操作，会影响本地数据，可能存在重复加密的情况， 需要clone成新bean，必须要有默认构造器
        //属性的二次操作 通过线程副本来解决 比如id的返回 此处需要将result和bean的映射放入线程副本
        Object result = clone(bean);
        CryptHelper.setKeyGenerateReference(bean, result);

        List<CryptFiled> filedList = CLASS_ENCRYPT_MAP.computeIfAbsent(result.getClass(), this::getEncryptFields);
        filedList.forEach(cryptFiled -> {
            try {
                cryptFiled.field.setAccessible(true);
                Object obj = cryptFiled.field.get(result);
                if (Objects.nonNull(obj)) {
                    Object encrypted = CryptHandlerFactory.getCryptHandler(obj, cryptFiled.cryptField).encrypt(obj,
                            cryptFiled.cryptField);
                    cryptFiled.field.set(result, encrypted);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    private List<CryptFiled> getEncryptFields(Class<?> cls) {
        List<CryptFiled> filedList = new ArrayList<>();
        if (Objects.isNull(cls)) {
            return filedList;
        }

        Field[] objFields = cls.getDeclaredFields();
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (Objects.nonNull(cryptField) && cryptField.encrypt()) {
                filedList.add(new CryptFiled(cryptField, field));
            }
        }
        return filedList;
    }

    private List<CryptFiled> getDecryptFields(Class<?> cls) {
        List<CryptFiled> filedList = new ArrayList<>();
        if (Objects.isNull(cls)) {
            return filedList;
        }

        Field[] objFields = cls.getDeclaredFields();
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (cryptField != null && cryptField.decrypt()) {
                filedList.add(new CryptFiled(cryptField, field));
            }
        }
        return filedList;
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        if (Objects.isNull(param)) {
            return null;
        }
        List<CryptFiled> filedList = CLASS_DECRYPT_MAP.computeIfAbsent(param.getClass(), this::getDecryptFields);
        filedList.forEach(cryptFiled -> {
            try {
                cryptFiled.field.setAccessible(true);
                Object obj = cryptFiled.field.get(param);
                if (Objects.nonNull(obj)) {
                    Object decrypted = CryptHandlerFactory.getCryptHandler(obj, cryptFiled.cryptField).decrypt(obj,
                            cryptFiled.cryptField);
                    cryptFiled.field.set(param, decrypted);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return param;
    }

    /**
     * 加密字段
     */
    private static class CryptFiled {

        private final Field field;
        private final CryptField cryptField;

        private CryptFiled(CryptField cryptField, Field field) {
            this.cryptField = cryptField;
            this.field = field;
        }
    }
}
