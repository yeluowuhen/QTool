package com.yeluo.file;

import com.yeluo.file.client.BaseClient;
import com.yeluo.file.client.FtpClient;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TestFtp
 *
 * @author wyb
 * @date 2021/2/2
 */
public class TestFile {

    public static void main(String[] args) {
        ShareParam smbSetting = new ShareParam("smb", "192.168.1.166", null,
                "test1", "123456wyb", null, "/test", new HashMap<>());

        ShareParam ftpSetting = new ShareParam("ftp", "192.168.1.166", null,
                "test1", "123456wyb", null, "/test", new HashMap<>());

//        testMethod(smbSetting, new SmbClient());

        for (int i = 0; i < 5; i++) {
            System.out.println();
        }

        testMethod(ftpSetting, new FtpClient());

    }

    private static void testMethod(ShareParam setting, BaseClient client) {
        String mp4 = "e:/tt/11.mp4";
        String mp44 = "/11/11.mp4";
        String localFile1 = "e:/tt/1.txt";
        String localFile2 = "e:/tt/777.txt";
        String shareFile1 = "/11/GF0101_20210103.SATECI";
        String shareDir2 = "/11/22/";
        String shareFile2 = shareDir2 + "2.txt";
        String shareFile3 = "/11/3.txt";
        String shareFile4 = "/11/4.bak";
        String shareFile5 = "/11/5.bak";
        String shareFile6 = "/11/6.bak";

        System.out.println("============= 测试upload/download =============================");

//        upload(client, setting, mp4, mp44);

//        for (int i = 0; i < 1; i++) {
//            upload(client, setting, localFile1, shareFile1);
//            upload(client, setting, localFile1, shareFile2);
//            download(client, setting, shareFile1, localFile2);
//        }
//        System.out.println("============= 测试hasFile/deleteFile/deleteDir =============================");
//        hasFile(client, setting, shareFile1);
//        deleteFile(client, setting, shareFile2);
//        deleteDir(client, setting, shareDir2);
//
//        System.out.println("============= 测试readFileToString/writeStringToFile =============================");
//        writeStringToFile(client, setting, shareFile3, "11111");
//        readFileToString(client, setting, shareFile3);
//        String data = readFileToString(client, setting, shareFile1);
//        writeStringToFile(client, setting, shareFile4, data);

        System.out.println("============= 测试readLines/writeLines =============================");
        List<String> lines = readLines(client, setting, shareFile1);
        writeLines(client, setting, shareFile5, lines);
        List<String> lines5 = readLines(client, setting, shareFile5);
        System.out.println("line5 (size)  ->  " + lines5.size());

//        System.out.println("============= 测试writeLinesByStream/readFileByStream =============================");
//        writeLinesByStream(client, setting, shareFile6, lines);
//        List<String> lines6 = readFileByStream(client, setting, shareFile6);
//        System.out.println("line6 (size)  ->  " + lines6.size());
    }


    private static void upload(BaseClient client, ShareParam setting, String soureFilePath, String targetFilePath) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.uploadFile(setting, soureFilePath, targetFilePath);
            System.out.println("upload状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void download(BaseClient client, ShareParam setting, String soureFilePath, String targetFilePath) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.downloadFile(setting, soureFilePath, targetFilePath);
            System.out.println("download状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hasFile(BaseClient client, ShareParam setting, String filePath) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.hasFile(setting, filePath);
            System.out.println("hasFile状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(BaseClient client, ShareParam setting, String filePath) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.deleteFile(setting, filePath);
            System.out.println("deleteFile状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDir(BaseClient client, ShareParam setting, String dirPath) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.deleteDir(setting, dirPath);
            System.out.println("deleteDir状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFileToString(BaseClient client, ShareParam setting, String filePath) {
        String data = null;
        try {
            long l = System.currentTimeMillis();
            data = client.readFileToString(setting, filePath, "utf-8");
            System.out.println("readFileToString（" + filePath + "） 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static List<String> readLines(BaseClient client, ShareParam setting, String filePath) {
        long l = System.currentTimeMillis();
        List<String> lines = new ArrayList<>();
        try {
            lines = client.readLines(setting, filePath, "utf-8");
            System.out.println("readLines （" + filePath + "） 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static List<String> readFileByStream(BaseClient client, ShareParam setting, String filePath) {
        long l = System.currentTimeMillis();
        List<String> list = new ArrayList<>();
        try {
            client.readFileByStream(setting, filePath, (inputStream) -> {
                try {
                    list.addAll(IOUtils.readLines(inputStream, "utf-8"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("writeLinesByStream （" + filePath + "） 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private static void writeStringToFile(BaseClient client, ShareParam setting, String filePath, String data) {
        try {
            long l = System.currentTimeMillis();
            client.writeStringToFile(setting, filePath, "utf-8", data);
            System.out.println("writeStringToFile （" + filePath + "） 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeLines(BaseClient client, ShareParam setting, String filePath, List<String> lines) {
        try {
            long l = System.currentTimeMillis();

            client.writeLines(setting, filePath, "utf-8", lines);
            System.out.println("writeLines （" + filePath + "） 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeLinesByStream(BaseClient client, ShareParam setting, String filePath, List<String> lines) {
        long l = System.currentTimeMillis();
        try {
            client.writeLinesByStream(setting, filePath, (outputStream) -> {
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                    for (String s : lines) {
                        bw.write(s);
                        bw.newLine();
                    }
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("writeLinesByStream （" + filePath + "） 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
