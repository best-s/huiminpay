package com.huiminpay.merchant;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OssTest {

    // 测试上传文件到阿里云存储空间
    @Test
    public void testUpload() throws FileNotFoundException {
        // 地域节点
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // accessKeyId
        String accessKeyId = "";
        // accessKeySecret
        String accessKeySecret = "";
        // 存储空间名称
        String bucketName = "huiminpaytest";
        // 上传后文件的名字
        String filename = "111.jpg";
        // 文件路径
        String filePath = "E:\\yunhe\\训练营\\代码\\img\\img.jpg";
        InputStream inputStream = new FileInputStream(filePath);

        // 创建oss客户端对象
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId , accessKeySecret);
        // 上传
        ossClient.putObject(bucketName, filename, inputStream);
        // 输出文件的网络地址
        System.out.println("https://" + bucketName + "." + endpoint +"/" + filename);
    }

}
