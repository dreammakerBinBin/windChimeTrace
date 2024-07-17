package com.windchime.boot.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

public class Base64Utils {
    private static String PicToBase64(File file) throws Exception {
        InputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] result = null;
        byte[] buf = new byte[1024];
        //用来定义一个准备接收图片总长度的局部变量
        int len;
        //将流的内容读取到buf内存中
        while ((len = in.read(buf)) > 0) {
            //将buf内存中的内容从0开始到总长度输出出去
            out.write(buf, 0, len);
        }
        //将out中的流内容拷贝到一开始定义的字节数组中
        result = out.toByteArray();
        //通过util包中的Base64类对字节数组进行base64编码
        return Base64.getEncoder().encodeToString(result);
    }
}
