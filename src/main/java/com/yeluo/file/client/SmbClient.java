package com.yeluo.file.client;

import com.yeluo.file.ShareParam;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 客户端-SMB
 *
 * @author wyb
 */
public class SmbClient extends BaseClient {

    private static final int BUFFER_SIZE = 1024 * 1024;

    static {
        //设置连接共享密码
        jcifs.Config.setProperty("jcifs.smb.client.disablePlainTextPasswords", "false");
    }

    /**
     * 创建连接
     *
     * @param shareParam 共享参数
     * @return smb连接
     * @throws IOException
     */
    private SmbFile smbInit(ShareParam shareParam, String filePath) throws IOException {
        // 远程端口
        int remotePort = shareParam.getRemotePort() != null ? shareParam.getRemotePort() : 445;
        // 拼接路径
        String path = (shareParam.getBasePath() != null ? shareParam.getBasePath() : "") + "/" + filePath;
        // 格式化路径
        path = path.replaceAll("\\\\", "/").replaceAll("//+", "/");

        // smb地址
        String smbUrl = String.format("smb://%s:%d/%s", shareParam.getRemoteUrl(), remotePort, path);

        // smb地址
        SmbFile smbFile = new SmbFile(smbUrl, authentication(shareParam));
        smbFile.connect();

        return smbFile;

    }

    /**
     * 用户认证
     *
     * @param shareParam 共享参数
     * @return 认证数据
     */
    private NtlmPasswordAuthentication authentication(ShareParam shareParam) {
        // 用户认证
        return new NtlmPasswordAuthentication(null, shareParam.getUsername(), shareParam.getPassword());

    }

    /**
     * 验证父文件夹是否存在，不存在则创建
     *
     * @param smbFile smb文件
     * @throws IOException
     */
    private void createParentDirectory(SmbFile smbFile) throws IOException {
        SmbFile file = new SmbFile(smbFile.getParent(), (NtlmPasswordAuthentication) smbFile.getPrincipal());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public boolean hasFile(ShareParam shareParam, String filePath) throws IOException {
        return smbInit(shareParam, filePath).exists();

    }

    @Override
    public boolean deleteFile(ShareParam shareParam, String filePath) throws IOException {
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        if (smbFile.exists()) {
            smbFile.delete();
        }
        return true;
    }

    @Override
    public boolean deleteDir(ShareParam shareParam, String dirPath) throws IOException {
        //连接共享初始化
        if (!dirPath.endsWith("/")) {
            dirPath += "/";
        }
        SmbFile smbFile = smbInit(shareParam, dirPath);
        if (smbFile.isDirectory()) {
            deleteDir(smbFile);
        } else {
            smbFile.delete();
        }
        return true;
    }

    private void deleteDir(SmbFile smbFile) throws IOException {
        for (SmbFile file : smbFile.listFiles()) {
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }

        }
        smbFile.delete();
    }

    @Override
    public boolean uploadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        boolean flag = false;
        // 定义流
        InputStream inputStream = null;
        OutputStream outputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, targetFilePath);
        try {
            // 验证父文件夹是否存在，不存在则创建
            createParentDirectory(smbFile);
//            smbFile.createNewFile();

            inputStream = new FileInputStream(sourceFilePath);
            outputStream = smbFile.getOutputStream();
            IOUtils.copy(inputStream, outputStream, BUFFER_SIZE);
            flag = true;

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
        return flag;
    }

    @Override
    public boolean downloadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        boolean flag = false;
        // 定义流
        InputStream inputStream = null;
        OutputStream outputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, sourceFilePath);
        try {
            if (!smbFile.exists()) {
                throw new FileNotFoundException("源文件不存在");
            }

            inputStream = smbFile.getInputStream();
            outputStream = new FileOutputStream(targetFilePath);
            IOUtils.copy(inputStream, outputStream, BUFFER_SIZE);
            flag = true;

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
        return flag;
    }

    @Override
    public String readFileToString(ShareParam shareParam, String filePath, String encoding) throws IOException {
        String context = null;
        // 定义流
        InputStream inputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        try {
            if (!smbFile.exists()) {
                throw new FileNotFoundException("源文件不存在");
            }

            inputStream = smbFile.getInputStream();
            context = IOUtils.toString(inputStream, encoding);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return context;
    }

    @Override
    public List<String> readLines(ShareParam shareParam, String filePath, String encoding) throws IOException {
        List<String> context = new ArrayList<>();
        // 定义流
        InputStream inputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        try {
            if (!smbFile.exists()) {
                throw new FileNotFoundException("源文件不存在");
            }

            inputStream = smbFile.getInputStream();
            context = IOUtils.readLines(inputStream, encoding);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return context;
    }

    @Override
    public void readFileByStream(ShareParam shareParam, String filePath, Consumer<InputStream> consumer) throws IOException {
        // 定义流
        InputStream inputStream = null;
        // 连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        try {
            if (!smbFile.exists()) {
                throw new FileNotFoundException("源文件不存在");
            }

            inputStream = smbFile.getInputStream();
            consumer.accept(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Override
    public void writeStringToFile(ShareParam shareParam, String filePath, String encoding, String data) throws IOException {
        // 定义流
        BufferedOutputStream outputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        try {
            // 验证父文件夹是否存在，不存在则创建
            createParentDirectory(smbFile);

            outputStream = new BufferedOutputStream(smbFile.getOutputStream());
            IOUtils.write(data, outputStream, encoding);

        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    @Override
    public void writeLines(ShareParam shareParam, String filePath, String encoding, List<String> lines) throws IOException {
        // 定义流
        BufferedOutputStream outputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        try {
            // 验证父文件夹是否存在，不存在则创建
            createParentDirectory(smbFile);

            outputStream = new BufferedOutputStream(smbFile.getOutputStream());
            IOUtils.writeLines(lines, null, outputStream, encoding);

        } finally {
            IOUtils.closeQuietly(outputStream);
        }

    }

    @Override
    public void writeLinesByStream(ShareParam shareParam, String filePath, Consumer<OutputStream> consumer) throws IOException {
        // 定义流
        OutputStream outputStream = null;
        //连接共享初始化
        SmbFile smbFile = smbInit(shareParam, filePath);
        try {
            // 验证父文件夹是否存在，不存在则创建
            createParentDirectory(smbFile);

            outputStream = smbFile.getOutputStream();
            consumer.accept(outputStream);

        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }


}
