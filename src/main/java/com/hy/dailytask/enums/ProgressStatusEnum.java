package com.hy.dailytask.enums;

public enum ProgressStatusEnum {

    NOTSTART(0, "未进行"),
    START(1, "进行中"),
    FINISH(2, "已完成"),
    TIMEOUT(3, "已超时");

    private final int key;
    private final String value;

    // 构造函数，枚举的构造函数默认为私有
    ProgressStatusEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    // 获取key
    public int getKey() {
        return key;
    }

    // 获取value
    public String getValue() {
        return value;
    }

    // 根据key获取对应的value
    public static String getValueByKey(int key) {
        for (ProgressStatusEnum status : ProgressStatusEnum.values()) {
            if (status.getKey() == key) {
                return status.getValue();
            }
        }
        return null; // 如果没有找到对应的key，返回null
    }
}
