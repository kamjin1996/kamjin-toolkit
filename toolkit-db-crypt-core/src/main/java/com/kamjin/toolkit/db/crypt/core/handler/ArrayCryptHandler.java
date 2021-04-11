package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;

import java.util.Arrays;
import java.util.Objects;

/**
 * 处理 Array 对象的加解密
 *
 * @author kamjin1996
 */
public class ArrayCryptHandler implements CryptHandler<Object> {

    @Override
    public Object encrypt(Object object, CryptField cryptField) {
        if (Objects.isNull(object)) {
            return null;
        }
        return Arrays.stream((Object[]) object)
                .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).encrypt(item, cryptField)).toArray();
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        if (Objects.isNull(param)) {
            return null;
        }
        return Arrays.stream((Object[]) param)
                .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).decrypt(item, cryptField)).toArray();
    }
}
