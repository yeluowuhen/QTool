package com.yeluo.file.client;

import com.yeluo.file.ShareParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * 客户端-本地
 *
 * @author wyb
 */
public class LocalClient extends BaseClient {

    @Override
    public boolean hasFile(ShareParam shareParam, String filePath) throws IOException {
        return (new File(formatPath(shareParam.getBasePath(), filePath))).exists();
    }

    @Override
    public boolean deleteFile(ShareParam shareParam, String filePath) throws IOException {
        File file = new File(formatPath(shareParam.getBasePath(), filePath));
        if (file.exists() && file.isFile()) {
            return FileUtils.deleteQuietly(file);
        }
        return false;
    }

    @Override
    public boolean deleteDir(ShareParam shareParam, String dirPath) throws IOException {
        File dir = new File(formatPath(shareParam.getBasePath(), dirPath));
        if (dir.exists() && dir.isDirectory()) {
            return FileUtils.deleteQuietly(dir);
        }
        return false;
    }

    @Override
    public boolean uploadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        FileUtils.copyFile(new File(sourceFilePath), new File(formatPath(shareParam.getBasePath(), targetFilePath)));
        return true;
    }

    @Override
    public boolean downloadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        if(!new File(formatPath(shareParam.getBasePath(), sourceFilePath)).exists()){
            throw new FileNotFoundException("源文件不存在");
        }
        FileUtils.copyFile(new File(formatPath(shareParam.getBasePath(), sourceFilePath)), new File(targetFilePath));
        return true;
    }

    @Override
    public String readFileToString(ShareParam shareParam, String filePath, String encoding) throws IOException {
        if(!new File(formatPath(shareParam.getBasePath(), filePath)).exists()){
            throw new FileNotFoundException("源文件不存在");
        }
        return FileUtils.readFileToString(new File(formatPath(shareParam.getBasePath(), filePath)), encoding);
    }

    @Override
    public List<String> readLines(ShareParam shareParam, String filePath, String encoding) throws IOException {
        if(!new File(formatPath(shareParam.getBasePath(), filePath)).exists()){
            throw new FileNotFoundException("源文件不存在");
        }
        return FileUtils.readLines(new File(formatPath(shareParam.getBasePath(), filePath)), encoding);
    }

    @Override
    public void readFileByStream(ShareParam shareParam, String filePath, Consumer<InputStream> consumer) throws IOException {
        if(!new File(formatPath(shareParam.getBasePath(), filePath)).exists()){
            throw new FileNotFoundException("源文件不存在");
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(formatPath(shareParam.getBasePath(), filePath));
            consumer.accept(inputStream);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Override
    public void writeStringToFile(ShareParam shareParam, String filePath, String encoding, String data) throws IOException {
        FileUtils.writeStringToFile(new File(formatPath(shareParam.getBasePath(), filePath)), data, encoding);
    }

    @Override
    public void writeLines(ShareParam shareParam, String filePath, String encoding, List<String> lines) throws IOException {
        FileUtils.writeLines(new File(formatPath(shareParam.getBasePath(), filePath)), encoding, lines);
    }

    @Override
    public void writeLinesByStream(ShareParam shareParam, String filePath, Consumer<OutputStream> consumer) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(formatPath(shareParam.getBasePath(), filePath));
            consumer.accept(outputStream);

        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * 格式化拼接文件路径
     * @param basePath 文件基础路径
     * @param filePath 文件路径
     * @return 输出全路径
     */
    private String formatPath(String basePath, String filePath){
        return basePath + File.separator + filePath;
    }

}
