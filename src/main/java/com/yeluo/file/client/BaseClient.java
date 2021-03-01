package com.yeluo.file.client;

import com.yeluo.file.ShareParam;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * 共享文件基类
 *
 * @author wyb
 */
public abstract class BaseClient {

    /**
     * 判断文件是否存在
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @return 是否存在
     * @throws IOException IO异常
     */
    public abstract boolean hasFile(ShareParam shareParam, String filePath) throws IOException;

    /**
     * 删除文件
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @throws IOException IO异常
     */
    public abstract boolean deleteFile(ShareParam shareParam, String filePath) throws IOException;

    /**
     * 删除文件夹
     *
     * @param shareParam 共享设置
     * @param dirPath      文件夹路径
     * @throws IOException IO异常
     */
    public abstract boolean deleteDir(ShareParam shareParam, String dirPath) throws IOException;

    /**
     * 删除缓存文件/文件夹
     *
     * @param shareParam 共享设置
     * @param tempFile     缓存文件
     * @throws IOException IO异常
     */
    public void deleteTempFile(ShareParam shareParam, File tempFile) throws IOException {
        FileUtils.forceDelete(tempFile);
    }

    /**
     * 上传文件
     *
     * @param shareParam   共享设置
     * @param sourceFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     * @throws IOException IO异常
     */
    public abstract boolean uploadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException;

    /**
     * 下载文件
     *
     * @param shareParam   共享设置
     * @param sourceFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     * @throws IOException IO异常
     */
    public abstract boolean downloadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException;

    /**
     * 读取文件返回列表
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     编码格式
     * @return 读取的数据内容
     * @throws IOException IO异常
     */
    public abstract String readFileToString(ShareParam shareParam, String filePath, String encoding) throws IOException;

    /**
     * 读取文件返回列表
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     编码格式
     * @return 读取的数据列表
     * @throws IOException IO异常
     */
    public abstract List<String> readLines(ShareParam shareParam, String filePath, String encoding) throws IOException;

    /**
     * 读取文件返回列表
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param consumer     处理过程
     * @return 读取的数据列表
     * @throws IOException IO异常
     */
    public abstract void readFileByStream(ShareParam shareParam, String filePath, Consumer<InputStream> consumer) throws IOException;


    /**
     * 写入字符串到文件中
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     编码格式
     * @param data         数据
     * @throws IOException IO异常
     */
    public abstract void writeStringToFile(ShareParam shareParam, String filePath, String encoding, String data) throws IOException;

    /**
     * 写入列表到文件中
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param encoding     编码格式
     * @param lines        数据列表
     * @throws IOException IO异常
     */
    public abstract void writeLines(ShareParam shareParam, String filePath, String encoding, List<String> lines) throws IOException;

    /**
     * 写入列表到文件中
     *
     * @param shareParam 共享设置
     * @param filePath     文件路径
     * @param consumer     处理过程
     * @throws IOException IO异常
     */
    public abstract void writeLinesByStream(ShareParam shareParam, String filePath, Consumer<OutputStream> consumer) throws IOException;

}
