package com.yeluo.string;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 字符串处理工具类
 */
public class StringUtil {

    /**
     * 根据空格切分字符串
     *
     * @param str
     * @return
     */
    public static String[] splitStrByBlank(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, " ");
        List<String> list = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list.toArray(new String[0]);
    }

}
