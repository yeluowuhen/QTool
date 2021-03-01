package com.yeluo.file;

import com.yeluo.file.client.*;

import java.io.IOException;

/**
 * 共享文件工厂类
 *
 * @author wyb
 */
public abstract class ClientFactory {

    /**
     * 根据类型创建客户端
     *
     * @param shareType 共享类型
     * @return 共享文件
     */
    public static BaseClient build(ShareTypeEnum shareType) throws IOException {
        BaseClient client = null;
        switch (shareType) {
            case LOCAL:
                client = new LocalClient();
                break;
            case FTP:
                client = new FtpClient();
                break;
            case SMB:
                client = new SmbClient();
                break;
            case ALIOSS:
                client = new AliOssClient();
                break;

            default:
                throw new IOException("暂不支持的共享类型[" + shareType.toString() + "]");
        }

        return client;
    }

    /**
     * 根据类型创建客户端
     *
     * @param shareType 共享类型
     * @return 共享文件
     */
    public static BaseClient build(String shareType) throws IOException {
        ShareTypeEnum shareTypeEnum = ShareTypeEnum.getEnumByStr(shareType);
        if (shareTypeEnum == null) {
            throw new IOException("暂不支持的共享类型[" + shareType + "]");
        }
        return build(shareTypeEnum);
    }


}
