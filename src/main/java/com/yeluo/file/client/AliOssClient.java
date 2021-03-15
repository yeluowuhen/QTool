package com.yeluo.file.client;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.yeluo.file.ShareParam;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 客户端-阿里云OSS
 *
 * @author wyb
 */
public class AliOssClient extends BaseClient {

    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * 创建OSS连接
     *
     * @param shareParam 共享参数
     * @return OSS连接
     */
    private OSS openOssClient(ShareParam shareParam) {
        return new OSSClientBuilder().build(shareParam.getRemoteUrl(), shareParam.getUsername(), shareParam.getPassword());

    }

    /**
     * 关闭OSS连接
     *
     * @param ossClient OSS连接
     */
    private void closeOssClient(OSS ossClient) {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * 验证父文件夹是否存在，不存在则创建
     *
     * @param ossClient  smb文件
     * @param bucketName 桶名
     * @param basePath   基础路径
     * @param filePath   文件路径
     */
    private void createParentDirectory(OSS ossClient, String bucketName, String basePath, String filePath) {

        String parentDir = getDirPathByPath(basePath, filePath);

        String[] arr = parentDir.split("/");

        String objName = "";
        ByteArrayInputStream inputStream = null;
        for (String s : arr) {
            if (!"".equalsIgnoreCase(s.trim())) {
                objName += s.trim();
                if (!ossClient.doesObjectExist(bucketName, objName)) {
                    try {
                        inputStream = new ByteArrayInputStream(new byte[0]);
                        ossClient.putObject(new PutObjectRequest(bucketName, objName, inputStream));
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasFile(ShareParam shareParam, String filePath) throws IOException {
        boolean flag = false;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        OSS ossClient = openOssClient(shareParam);
        try {
            flag = ossClient.doesObjectExist(bucketName, objPath);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            closeOssClient(ossClient);
        }
        return flag;

    }

    @Override
    public boolean deleteFile(ShareParam shareParam, String filePath) throws IOException {
        boolean flag = false;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        OSS ossClient = openOssClient(shareParam);
        try {
            if (ossClient.doesObjectExist(bucketName, objPath)) {
                ossClient.deleteObject(bucketName, objPath);
                flag = true;
            }
        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            closeOssClient(ossClient);
        }

        return flag;
    }

    @Override
    public boolean deleteDir(ShareParam shareParam, String dirPath) throws IOException {
        boolean flag = true;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), dirPath);

        OSS ossClient = openOssClient(shareParam);
        try {
            if (ossClient.doesObjectExist(bucketName, objPath)) {
                ossClient.deleteObject(bucketName, objPath);
                flag = true;
            }
        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            closeOssClient(ossClient);
        }

        return flag;
    }

    @Override
    public boolean uploadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        boolean flag = false;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), targetFilePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objPath, new File(sourceFilePath));
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            flag = true;

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            closeOssClient(ossClient);

        }
        return flag;

    }

    @Override
    public boolean downloadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        boolean flag = false;

        InputStream inputStream = null;
        OutputStream outputStream = null;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), sourceFilePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {

            File targetFile = new File(targetFilePath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
//            ObjectMetadata object = ossClient.getObject(new GetObjectRequest(bucketName, objPath), targetFile);

            OSSObject ossObject = ossClient.getObject(bucketName, objPath);
            inputStream = ossObject.getObjectContent();
            outputStream = new FileOutputStream(targetFile);
            IOUtils.copy(inputStream, outputStream, BUFFER_SIZE);

            flag = true;

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            closeOssClient(ossClient);

        }

        return flag;

    }

    @Override
    public String readFileToString(ShareParam shareParam, String filePath, String encoding) throws IOException {
        String context = null;

        InputStream inputStream = null;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objPath);
            inputStream = ossObject.getObjectContent();
            context = IOUtils.toString(inputStream, encoding);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeOssClient(ossClient);
        }

        return context;

    }

    @Override
    public List<String> readLines(ShareParam shareParam, String filePath, String encoding) throws IOException {
        List<String> context = new ArrayList<>();

        InputStream inputStream = null;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objPath);
            inputStream = ossObject.getObjectContent();
            context = IOUtils.readLines(inputStream, encoding);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeOssClient(ossClient);
        }

        return context;

    }

    @Override
    public void readFileByStream(ShareParam shareParam, String filePath, Consumer<InputStream> consumer) throws IOException {
        InputStream inputStream = null;
        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objPath);
            inputStream = ossObject.getObjectContent();
            consumer.accept(inputStream);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeOssClient(ossClient);
        }

    }

    @Override
    public void writeStringToFile(ShareParam shareParam, String filePath, String encoding, String data) throws IOException {
        InputStream inputStream = null;

        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {
            inputStream = IOUtils.toInputStream(data, encoding);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objPath, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            closeOssClient(ossClient);

        }

    }

    @Override
    public void writeLines(ShareParam shareParam, String filePath, String encoding, List<String> lines) throws IOException {
        InputStream inputStream = null;

        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {
            String data = String.join(IOUtils.LINE_SEPARATOR, lines);
            inputStream = new ByteArrayInputStream(data.getBytes(encoding));
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objPath, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            IOUtils.closeQuietly(inputStream);
            closeOssClient(ossClient);

        }

    }

    @Override
    public void writeLinesByStream(ShareParam shareParam, String filePath, Consumer<OutputStream> consumer) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        // 桶名
        String bucketName = shareParam.getBucketName();
        // 对象地址
        String objPath = formatOssPath(shareParam.getBasePath(), filePath);

        // 获取连接
        OSS ossClient = openOssClient(shareParam);
        try {
            outputStream = new ByteArrayOutputStream();
            consumer.accept(outputStream);
            inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objPath, inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);

        } catch (OSSException | ClientException ossException) {
            throw new IOException("OSSException：" + ossException.getMessage(), ossException);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            closeOssClient(ossClient);
        }

    }


    /**
     * 格式化Ftp路径
     *
     * @param filePath 文件路径
     * @return 格式化后的文件路径
     */
    public static String formatOssPath(String basePath, String filePath) {
        String path = basePath + "/" + filePath;
        return path.replaceAll("\\\\", "/").replaceAll("//+", "/");
    }

    /**
     * 根据文件路径获取最后的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileNameByPath(String basePath, String filePath) {
        String path = formatOssPath(basePath, filePath);
        return path.lastIndexOf("/") > 0 ? path.substring(path.lastIndexOf("/") + 1) : path;

    }

    /**
     * 根据文件路径获取文件夹路径
     *
     * @param filePath 文件路径
     * @return 文件夹路径
     */
    public static String getDirPathByPath(String basePath, String filePath) {
        String path = formatOssPath(basePath, filePath);
        return path.substring(0, path.lastIndexOf("/"));

    }


}
