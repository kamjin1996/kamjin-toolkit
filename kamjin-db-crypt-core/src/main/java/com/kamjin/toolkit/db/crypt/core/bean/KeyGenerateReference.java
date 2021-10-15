package com.kamjin.toolkit.db.crypt.core.bean;

/**
 * @author kam
 * @since 2021/4/10
 *
 * <p>
 * ID生成的引用
 * </p>
 */
public class KeyGenerateReference {

    /**
     * 原来插入时，引用的数据库bean对象
     */
    private Object originPojo;

    /**
     * 克隆的bean 也就是代码中继续使用的bean
     */
    private Object clonePojo;

    public Object getOriginPojo() {
        return originPojo;
    }

    public void setOriginPojo(Object originPojo) {
        this.originPojo = originPojo;
    }

    public Object getClonePojo() {
        return clonePojo;
    }

    public void setClonePojo(Object clonePojo) {
        this.clonePojo = clonePojo;
    }
}
