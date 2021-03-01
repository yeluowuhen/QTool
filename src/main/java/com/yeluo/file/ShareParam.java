package com.yeluo.file;

import java.io.Serializable;
import java.util.Map;

/**
 * 共享参数设置
 *
 * @author wyb
 */
public class ShareParam implements Serializable {

    private static final long serialVersionUID = -8636080798001826021L;

    /**
     * 共享存储类型【type】
     */
    private String shareType;
    /**
     * 共享存储地址
     */
    private String remoteUrl;
    /**
     * 共享存储端口
     */
    private Integer remotePort;
    /**
     * 用户名/AK
     */
    private String username;
    /**
     * 密码/SK
     */
    private String password;
    /**
     * 存储桶
     */
    private String bucketName;
    /**
     * 基础路径
     */
    private String basePath;
    /**
     * 其他参数信息
     */
    private Map<String, Object> params;

    public ShareParam(String shareType, String remoteUrl, Integer remotePort, String username, String password,
                      String bucketName, String basePath, Map<String, Object> params) {
        this.shareType = shareType;
        this.remoteUrl = remoteUrl;
        this.remotePort = remotePort;
        this.username = username;
        this.password = password;
        this.bucketName = bucketName;
        this.basePath = basePath;
        this.params = params;
    }

    public ShareParam() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ShareSetting{" +
                "shareType='" + shareType + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", remotePort=" + remotePort +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", basePath='" + basePath + '\'' +
                ", params=" + params +
                '}';
    }
}
