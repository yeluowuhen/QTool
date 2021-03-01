package com.yeluo.file;

import com.yeluo.file.client.BaseClient;
import com.yeluo.file.client.SmbClient;
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
public class TestSmb {

    public static void main(String[] args) {
        ShareParam setting = new ShareParam("smb", "10.55.28.15", null,
                "hangtianyingyong", "hangtianyingyong", null, "hangtianyingyong/王玉彬/共享/测试", new HashMap<>());

        BaseClient client = new SmbClient();

        for (int i = 0; i < 10; i++) {
            upload(client, setting);

            download(client, setting);
        }

        hasFile(client, setting);
        hasFile2(client, setting);

//        deleteFile(client,setting);

        deleteDir(client,setting);

        writeStringToFile(client, setting);
        readFileToString(client, setting);

        writeLines(client, setting);
        readLines(client, setting);

        writeLinesByStream(client, setting);
        readFileByStream(client, setting);

    }

    private static void download(BaseClient client, ShareParam setting) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.downloadFile(setting, "\\卫星轨道数据/ForecastData/GF0101/20210103/GF0101_20210103.SATECI", "c:/tt/777.txt");
            System.out.println("download状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void upload(BaseClient client, ShareParam setting) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.uploadFile(setting, "c:/tt/阿斯.txt", "\\1/2/身份卡999.txt");
            System.out.println("upload状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hasFile(BaseClient client, ShareParam setting) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.hasFile(setting, "1/2/身份卡999.txt");
            System.out.println("hasFile状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hasFile2(BaseClient client, ShareParam setting) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.hasFile(setting, "1/3/身份卡999.txt");
            System.out.println("hasFile状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(BaseClient client, ShareParam setting) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.deleteFile(setting, "1/2/身份卡999.txt");
            System.out.println("deleteFile状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDir(BaseClient client, ShareParam setting) {
        boolean b = false;
        try {
            long l = System.currentTimeMillis();
            b = client.deleteDir(setting, "1");
            System.out.println("deleteDir状态：" + b + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeStringToFile(BaseClient client, ShareParam setting) {
        try {
            long l = System.currentTimeMillis();
            client.writeStringToFile(setting, "1/存储/1@1.txt", "utf-8", "测试是是是\r\nashfkjashf");
            System.out.println("writeStringToFile 状态：" + "  耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFileToString(BaseClient client, ShareParam setting) {
        try {
            long l = System.currentTimeMillis();
            String string = client.readFileToString(setting, "1/存储/1@1.txt", "utf-8");
            System.out.println("readFileToString 内容: " + string);
            System.out.println("readFileToString 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeLines(BaseClient client, ShareParam setting) {
        try {
            long l = System.currentTimeMillis();
            List<String> lines = new ArrayList<>();
            lines.add("111111");
            lines.add("第三方大厦");
            lines.add("第三方大厦GF");

            client.writeLines(setting, "1/存储/1@2.txt", "utf-8", lines);
            System.out.println("writeLines 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readLines(BaseClient client, ShareParam setting) {
        try {
            long l = System.currentTimeMillis();
            List<String> lines = client.readLines(setting, "1/存储/1@2.txt", "utf-8");
            System.out.println("readLines 内容: ");
            lines.forEach(System.out::println);
            System.out.println("readLines 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void writeLinesByStream(BaseClient client, ShareParam setting) {
        try {
            long l = System.currentTimeMillis();

            List<String> list = new ArrayList<>();
            list.add("sdasfs");
            list.add("我是成都数据库");
            list.add("GF第三个");
            list.add("线程则发VR头发图书馆是大法官发多少");
            list.add("的说法都是富人的");
            client.writeLinesByStream(setting, "1/存储/1@3.txt", (outputStream) -> {
                try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                    for (String s : list) {
                        bw.write(s);
                        bw.newLine();
                    }
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    IOUtils.writeLines(list, null, outputStream, "utf-8");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            });
            System.out.println("writeLinesByStream 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFileByStream(BaseClient client, ShareParam setting) {
        try {
            long l = System.currentTimeMillis();

            List<String> list = new ArrayList<>();
            client.readFileByStream(setting, "1/存储/1@3.txt", (inputStream) -> {
                try {
                    list.addAll(IOUtils.readLines(inputStream, "utf-8"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("writeLinesByStream 内容: ");
            list.forEach(System.out::println);
            System.out.println("writeLinesByStream 耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
