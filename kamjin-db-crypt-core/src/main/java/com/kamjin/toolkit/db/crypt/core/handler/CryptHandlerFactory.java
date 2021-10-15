package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import com.kamjin.toolkit.db.crypt.core.util.CryptHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 加解密处理者工厂类
 *
 * @author kamjin1996
 */
@SuppressWarnings("all")
public class CryptHandlerFactory {

    private static final CryptHandler STRING_HANDLER = new StringCryptHandler();
    private static final CryptHandler COLLECTION_HANDLER = new CollectionCryptHandler();
    private static final CryptHandler LIST_HANDLER = new ListCryptHandler();
    private static final CryptHandler ARRAY_HANDLER = new ArrayCryptHandler();
    private static final CryptHandler BEAN_HANDLER = new BeanCryptHandler();
    private static final CryptHandler EMPTY_HANDLER = new EmptyCryptHandler();

    public static CryptHandler getCryptHandler(Object obj, CryptField cryptField) {
        // 如果是map不处理
        if (Objects.isNull(obj) || CryptHelper.inIgnoreClass(obj.getClass()) || obj instanceof Map) {
            return EMPTY_HANDLER;
        }

        if (obj instanceof String && Objects.isNull(cryptField)) {
            return EMPTY_HANDLER;
        }
        if (obj instanceof String) {
            return STRING_HANDLER;
        }

        if (obj instanceof List) {
            return LIST_HANDLER;
        }

        if (obj instanceof Collection) {
            return COLLECTION_HANDLER;
        }

        if (obj.getClass().isArray()) {
            return ARRAY_HANDLER;
        }
        return BEAN_HANDLER;
    }
}
