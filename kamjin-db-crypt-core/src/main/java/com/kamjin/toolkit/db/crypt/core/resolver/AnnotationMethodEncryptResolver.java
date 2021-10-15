package com.kamjin.toolkit.db.crypt.core.resolver;

import com.kamjin.toolkit.db.crypt.core.handler.CryptHandlerFactory;

import java.util.List;
import java.util.Map;

/**
 * 有注解的方法加密处理者
 *
 * @author kamjin1996
 */
public class AnnotationMethodEncryptResolver implements MethodEncryptResolver {

    private List<MethodAnnotationEncryptParameter> methodAnnotationEncryptParameterList;

    public AnnotationMethodEncryptResolver() {
    }

    public AnnotationMethodEncryptResolver(List<MethodAnnotationEncryptParameter> methodAnnotationEncryptParameterList) {
        this.methodAnnotationEncryptParameterList = methodAnnotationEncryptParameterList;
    }

    @Override
    public Object processEncrypt(Object param) {
        Map map = (Map) param;
        methodAnnotationEncryptParameterList
                .forEach(item -> map.computeIfPresent(item.getParamName(), (key, oldValue) -> CryptHandlerFactory
                        .getCryptHandler(oldValue, item.getCryptField()).encrypt(oldValue, item.getCryptField())));
        return param;
    }
}
