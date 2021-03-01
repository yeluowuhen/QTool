package com.yeluo.file;

/**
 * 计算类型枚举
 *
 * @author wyb
 */
public enum ShareTypeEnum {

    /**
     * 本地文件
     */
    LOCAL,
    /**
     * FTP
     */
    FTP,
    /**
     * 共享SMB
     */
    SMB,
    /**
     * 阿里云OSS
     */
    ALIOSS;

    /**
     * 根据字符串获取枚举对象
     * @param str 字符串
     * @return 枚举值
     */
    public static ShareTypeEnum getEnumByStr(String str) {
        for (ShareTypeEnum value : ShareTypeEnum.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return null;
    }

}
