package com.yeluo.file;

import com.yeluo.file.client.BaseClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * 共享文件工具类
 *
 * @author wyb
 * @date 2021/2/6
 */
public class ShareFileUtil {

    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = 1048576L;
    public static final long ONE_GB = 1073741824L;
    public static final String UTF_8 = "utf-8";
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * 获取客户端
     *
     * @param shareParam 共享配置信息
     * @return 客户端
     */
    private static BaseClient getShareFileBase(ShareParam shareParam) throws IOException {
        return ClientFactory.build(shareParam.getShareType());
    }

    /**
     * 判断文件是否存在
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @return 是否存在
     * @throws IOException IO异常
     */
    public static boolean hasFile(ShareParam shareParam, String filePath) throws IOException {
        return getShareFileBase(shareParam).hasFile(shareParam, filePath);
    }

    /**
     * 获取文件并将文件下载到缓存位置
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param tempFilePath 缓存文件地址
     * @return 是否存在
     * @throws IOException IO异常
     */
    public static File getFile(ShareParam shareParam, String filePath, String tempFilePath) throws IOException {
        BaseClient baseClient = getShareFileBase(shareParam);
        if (baseClient.hasFile(shareParam, filePath)) {
            baseClient.downloadFile(shareParam, filePath, tempFilePath);
            return new File(tempFilePath);
        }
        return null;

    }

    /**
     * 删除文件
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @return 执行状态
     * @throws IOException IO异常
     */
    public static boolean deleteFile(ShareParam shareParam, String filePath) throws IOException {
        return getShareFileBase(shareParam).deleteFile(shareParam, filePath);
    }

    /**
     * 上传文件
     *
     * @param shareParam   共享设置
     * @param sourceFilePath 上传文件路径
     * @param targetFilePath 存放路径
     * @throws IOException IO异常
     */
    public static void uploadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        getShareFileBase(shareParam).uploadFile(shareParam, sourceFilePath, targetFilePath);
    }

    /**
     * 下载文件
     *
     * @param shareParam   共享设置
     * @param sourceFilePath 下载文件路径
     * @param targetFilePath 存放路径
     * @throws IOException IO异常
     */
    public static void downloadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        getShareFileBase(shareParam).downloadFile(shareParam, sourceFilePath, targetFilePath);

    }

    /**
     * 读取文件写入到字符串
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     文件编码
     * @return 文件字符串
     * @throws IOException IO异常
     */
    public static String readFileToString(ShareParam shareParam, String filePath, String encoding) throws IOException {
        return getShareFileBase(shareParam).readFileToString(shareParam, filePath, encoding);
    }

    /**
     * 按行读取文件到列表中
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     文件编码
     * @return 文件数据列表
     * @throws IOException IO异常
     */
    public static List<String> readLines(ShareParam shareParam, String filePath, String encoding) throws IOException {
        return getShareFileBase(shareParam).readLines(shareParam, filePath, encoding);
    }

    /**
     * 流式读取文件
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param consumer     读取操作
     * @throws IOException IO异常
     */
    public static void readFileByStream(ShareParam shareParam, String filePath, Consumer<InputStream> consumer) throws IOException {
        getShareFileBase(shareParam).readFileByStream(shareParam, filePath, consumer);
    }

    /**
     * 写入字符串到指定文件
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     文件编码
     * @param data         字符串
     * @throws IOException IO异常
     */
    public static void writeStringToFile(ShareParam shareParam, String filePath, String encoding, String data) throws IOException {
        getShareFileBase(shareParam).writeStringToFile(shareParam, filePath, encoding, data);
    }

    /**
     * 写入字符串列表到指定文件
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     文件编码
     * @param lines        字符串列表
     * @throws IOException IO异常
     */
    public static void writeLines(ShareParam shareParam, String filePath, String encoding, List<String> lines) throws IOException {
        getShareFileBase(shareParam).writeLines(shareParam, filePath, encoding, lines);
    }

    /**
     * 流式写入文件
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param consumer     写入操作
     * @throws IOException IO异常
     */
    public static void writeLinesByStream(ShareParam shareParam, String filePath, Consumer<OutputStream> consumer) throws IOException {
        getShareFileBase(shareParam).writeLinesByStream(shareParam, filePath, consumer);
    }


}
