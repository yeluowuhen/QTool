package com.yeluo.file.client;

import com.yeluo.file.ShareParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 客户端-FTP
 *
 * @author wyb
 */
public class FtpClient extends BaseClient {

    /**
     * 本地编码
     */
    private static String LOCAL_CHARSET = "GBK";
    /**
     * FTP协议里，规定文件名编码为iso-8859-1
     */
    private static final String SERVER_CHARSET = "iso-8859-1";

    /**
     * 创建连接
     *
     * @param shareParam 共享参数
     * @return FTP连接
     * @throws IOException
     */
    private FTPClient openFtpClient(ShareParam shareParam) throws IOException {
        FTPClient ftpClient = new FTPClient();
        // 端口
        int remotePort = shareParam.getRemotePort() != null ? shareParam.getRemotePort() : 21;
        ftpClient.connect(shareParam.getRemoteUrl(), remotePort);

        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            ftpClient.disconnect();
            throw new IOException("FTP创建连接失败");
        }

        // 设置被动模式，解决在linux环境下，因为安全端口关闭而引起的问题
        ftpClient.enterLocalPassiveMode();
        // 设置以字节流传输模式
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8，否则就用本地编码
        if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))
                && !"UTF-8".equalsIgnoreCase(LOCAL_CHARSET)) {
            LOCAL_CHARSET = "UTF-8";

        } else if (!"GBK".equalsIgnoreCase(LOCAL_CHARSET)) {
            LOCAL_CHARSET = "GBK";
        }
        // 设置FTP控制连接使用的字符集，必须在连接前设置
        ftpClient.setControlEncoding(LOCAL_CHARSET);

        if (!ftpClient.login(shareParam.getUsername(), shareParam.getPassword())) {
            throw new IOException("FTP登录失败");
        }
        return ftpClient;
    }

    /**
     * 关闭FTP连接
     *
     * @param ftpClient FTP连接
     */
    private void closeFtpClient(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean hasFile(ShareParam shareParam, String filePath) throws IOException {
        boolean flag = false;
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            flag = hasFile(ftpClient, formatFtpPath(shareParam.getBasePath(), filePath));

        } finally {
            closeFtpClient(ftpClient);

        }
        return flag;

    }

    private boolean hasFile(FTPClient ftpClient, String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        if (ftpFiles != null && ftpFiles.length > 0) {
            flag = true;
        }
        return flag;

    }


    @Override
    public boolean deleteFile(ShareParam shareParam, String filePath) throws IOException {
        boolean flag = false;
        FTPClient ftpClient = openFtpClient(shareParam);
        String path = formatFtpPath(shareParam.getBasePath(), filePath);
        try {
            if (hasFile(ftpClient, path)) {
                flag = ftpClient.deleteFile(path);
            }

        } finally {
            closeFtpClient(ftpClient);
        }
        return flag;
    }

    @Override
    public boolean deleteDir(ShareParam shareParam, String dirPath) throws IOException {
        boolean flag = true;
        FTPClient ftpClient = openFtpClient(shareParam);
        String path = formatFtpPath(shareParam.getBasePath(), dirPath);
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(path);
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isDirectory()) {
                    boolean b = deleteDir(ftpClient, path + "/" + transcoding(ftpFile.getName(), LOCAL_CHARSET, SERVER_CHARSET));
                    flag = flag ? b : flag;
                } else {
                    boolean b = ftpClient.deleteFile(path + "/" + transcoding(ftpFile.getName(), LOCAL_CHARSET, SERVER_CHARSET));
                    flag = flag ? b : flag;
                }
            }

            flag = ftpClient.removeDirectory(path);
        } finally {
            closeFtpClient(ftpClient);
        }
        return flag;
    }

    private boolean deleteDir(FTPClient ftpClient, String dirPath) throws IOException {
        boolean flag = true;
        FTPFile[] ftpFiles = ftpClient.listFiles(dirPath);
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isDirectory()) {
                boolean b = deleteDir(ftpClient, dirPath + "/" + transcoding(ftpFile.getName(), LOCAL_CHARSET, SERVER_CHARSET));
                flag = flag ? b : flag;

            } else {
                boolean b = ftpClient.deleteFile(dirPath + "/" + transcoding(ftpFile.getName(), LOCAL_CHARSET, SERVER_CHARSET));
                flag = flag ? b : flag;

            }
        }
        return ftpClient.removeDirectory(dirPath);
    }

    @Override
    public boolean uploadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        boolean flag = false;
        BufferedInputStream inputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), targetFilePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), targetFilePath);
            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, true);

            inputStream = new BufferedInputStream(new FileInputStream(new File(sourceFilePath)));
            flag = ftpClient.storeFile(ftpfileName, inputStream);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeFtpClient(ftpClient);
        }

        return flag;
    }

    @Override
    public boolean downloadFile(ShareParam shareParam, String sourceFilePath, String targetFilePath) throws IOException {
        boolean flag = false;
        BufferedOutputStream outputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            if (!hasFile(ftpClient, formatFtpPath(shareParam.getBasePath(), sourceFilePath))) {
                throw new FileNotFoundException("源文件不存在");
            }
            // 文件目录
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), sourceFilePath);
            // 文件夹
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), sourceFilePath);

            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, false);

            File targetFile = new File(targetFilePath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(new File(targetFilePath)));
            flag = ftpClient.retrieveFile(ftpfileName, outputStream);

        } finally {
            IOUtils.closeQuietly(outputStream);
            closeFtpClient(ftpClient);
        }
        return flag;

    }

    @Override
    public String readFileToString(ShareParam shareParam, String filePath, String encoding) throws IOException {
        String context = null;
        InputStream inputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            if (!hasFile(ftpClient, formatFtpPath(shareParam.getBasePath(), filePath))) {
                throw new FileNotFoundException("源文件不存在");
            }
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), filePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), filePath);

            // 切换目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, false);

            inputStream = ftpClient.retrieveFileStream(ftpfileName);
            context = IOUtils.toString(inputStream, encoding);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeFtpClient(ftpClient);
        }
        return context;
    }

    @Override
    public List<String> readLines(ShareParam shareParam, String filePath, String encoding) throws IOException {
        List<String> context = new ArrayList<>();
        InputStream inputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            if (!hasFile(ftpClient, formatFtpPath(shareParam.getBasePath(), filePath))) {
                throw new FileNotFoundException("源文件不存在");
            }
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), filePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), filePath);

            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, false);

            inputStream = ftpClient.retrieveFileStream(ftpfileName);
            context = IOUtils.readLines(inputStream, encoding);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeFtpClient(ftpClient);
        }
        return context;
    }

    @Override
    public void readFileByStream(ShareParam shareParam, String filePath, Consumer<InputStream> consumer) throws IOException {
        InputStream inputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            if (!hasFile(ftpClient, formatFtpPath(shareParam.getBasePath(), filePath))) {
                throw new FileNotFoundException("源文件不存在");
            }
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), filePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), filePath);

            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, false);

            inputStream = ftpClient.retrieveFileStream(ftpfileName);
            consumer.accept(inputStream);

        } finally {
            IOUtils.closeQuietly(inputStream);
            closeFtpClient(ftpClient);
        }

    }

    @Override
    public void writeStringToFile(ShareParam shareParam, String filePath, String encoding, String data) throws IOException {
        BufferedOutputStream outputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), filePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), filePath);

            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, true);

            outputStream = new BufferedOutputStream(ftpClient.storeFileStream(ftpfileName));
            // 生成流
            IOUtils.write(data, outputStream, encoding);

        } finally {
            IOUtils.closeQuietly(outputStream);
            closeFtpClient(ftpClient);
        }
    }

    @Override
    public void writeLines(ShareParam shareParam, String filePath, String encoding, List<String> lines) throws IOException {
        BufferedOutputStream outputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), filePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), filePath);

            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, true);
            // 生成流
            outputStream = new BufferedOutputStream(ftpClient.storeFileStream(ftpfileName));
            IOUtils.writeLines(lines, null, outputStream, encoding);

        } finally {
            IOUtils.closeQuietly(outputStream);
            closeFtpClient(ftpClient);
        }
    }

    @Override
    public void writeLinesByStream(ShareParam shareParam, String filePath, Consumer<OutputStream> consumer) throws IOException {
        OutputStream outputStream = null;
        // 获取连接
        FTPClient ftpClient = openFtpClient(shareParam);
        try {
            String ftpDirPath = getFtpDirPathByFtpPath(shareParam.getBasePath(), filePath);
            String ftpfileName = getFtpFileNameByFtpPath(shareParam.getBasePath(), filePath);

            // 切换FTP目录，不存在则创建
            changeWorkingDirectory(ftpClient, ftpDirPath, true);
            // 生成流
            outputStream = ftpClient.storeFileStream(ftpfileName);
            consumer.accept(outputStream);

        } finally {
            IOUtils.closeQuietly(outputStream);
            closeFtpClient(ftpClient);
        }
    }

    /**
     * 切换工作目录
     *
     * @param ftpClient   ftp客户端
     * @param dirPath     目录
     * @param isCreateDir 是否创建空文件夹
     * @throws IOException
     */
    private void changeWorkingDirectory(FTPClient ftpClient, String dirPath, boolean isCreateDir) throws IOException {
        //切换到指定目录
        if (!ftpClient.changeWorkingDirectory(dirPath)) {
            if (isCreateDir) {
                //如果目录不存在则创建目录
                String[] dirs = dirPath.split("/");
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir.trim())) {
                        continue;
                    }
                    if (!ftpClient.changeWorkingDirectory(dir)) {
                        if (!ftpClient.makeDirectory(dir)) {
                            throw new IOException("创建文件夹异常");
                        }
                        if (!ftpClient.changeWorkingDirectory(dir)) {
                            throw new IOException("切换文件夹异常");
                        }
                    }
                }
            } else {
                throw new IOException("文件目录不存在");
            }

        }

    }


    /**
     * 格式化拼接Ftp路径
     *
     * @param basePath 文件基础路径
     * @param filePath 文件路径
     * @return 输出全路径
     */
    public static String formatFtpPath(String basePath, String filePath) throws UnsupportedEncodingException {
        String path = basePath + "/" + filePath;
        path = path.replaceAll("\\\\", "/").replaceAll("//+", "/");
        return transcoding(path, LOCAL_CHARSET, SERVER_CHARSET);
    }

    /**
     * 根据ftp文件路径获取最后的文件名
     *
     * @param basePath    基础路径
     * @param ftpFilePath 文件路径
     * @return 文件名
     */
    public static String getFtpFileNameByFtpPath(String basePath, String ftpFilePath) throws UnsupportedEncodingException {
        String path = formatFtpPath(basePath, ftpFilePath);
        return path.lastIndexOf("/") > 0 ? path.substring(path.lastIndexOf("/") + 1) : path;

    }

    /**
     * 根据ftp文件路径获取文件夹路径
     *
     * @param basePath    基础路径
     * @param ftpFilePath 文件路径
     * @return 文件夹路径
     */
    public static String getFtpDirPathByFtpPath(String basePath, String ftpFilePath) throws UnsupportedEncodingException {
        String path = formatFtpPath(basePath, ftpFilePath);
        return path.substring(0, path.lastIndexOf("/"));

    }

    /**
     * 字符串转码
     *
     * @param str          字符串
     * @param localChaset  本地编码
     * @param serverChaset 服务器编码
     * @return 转码后字符串
     * @throws UnsupportedEncodingException
     */
    public static String transcoding(String str, String localChaset, String serverChaset) throws UnsupportedEncodingException {
        return new String(str.getBytes(localChaset), serverChaset);
    }


}
