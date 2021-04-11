package com.kamjin.toolkit.db.crypt.core.util;

import com.kamjin.toolkit.db.crypt.core.bean.KeyGenerateReference;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 加解密帮助类
 * </p>
 */
public final class CryptHelper {

    /**
     * 线程副本存储ID 用以克隆的对象的ID赋值
     */
    private static final ThreadLocal<KeyGenerateReference> KEY_GENERATE_REFERENCE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 忽略的class列表
     */
    public static final Set<Class<?>> IGNORE_CLASS = new HashSet<>();

    static {
        // initIgnoreClass
        IGNORE_CLASS.add(Byte.class);
        IGNORE_CLASS.add(Short.class);
        IGNORE_CLASS.add(Integer.class);
        IGNORE_CLASS.add(Long.class);
        IGNORE_CLASS.add(Float.class);
        IGNORE_CLASS.add(Double.class);
        IGNORE_CLASS.add(Boolean.class);
        IGNORE_CLASS.add(Character.class);
    }

    public static boolean inIgnoreClass(Class<?> cls) {
        return IGNORE_CLASS.contains(cls);
    }

    /**
     * 放入keyGen的源对象和克隆对象
     *
     * @param originPojo 源对象，即调用插入方法时的入参对象
     * @param clonePojo  克隆对象 为避免原先的代码重复加密而克隆的对象
     */
    public static void setKeyGenerateReference(Object originPojo, Object clonePojo) {
        KeyGenerateReference reference = new KeyGenerateReference();
        reference.setOriginPojo(originPojo);
        reference.setClonePojo(clonePojo);
        KEY_GENERATE_REFERENCE_THREAD_LOCAL.set(reference);
    }

    /**
     * 获取keyGen的引用
     *
     * @return
     */
    public static KeyGenerateReference getKeyGenerateReference() {
        return KEY_GENERATE_REFERENCE_THREAD_LOCAL.get();
    }

    /**
     * 清理keyGen引用
     */
    public static void cleanKeyGenerateReference() {
        KEY_GENERATE_REFERENCE_THREAD_LOCAL.remove();
    }
}
