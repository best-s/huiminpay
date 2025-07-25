package com.huiminpay.merchant.service.Impl;

import com.aliyun.oss.OSS;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.merchant.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service // Spring注解
public class FileServiceImpl implements FileService {

    @Autowired
    OSS ossClient;
    @Value("${oss.aliyun.bucket}")
    String bucket;
    @Value("${oss.aliyun.domain}")
    String domain;

    /**
     * 上传文件到阿里云
     * @param file 文件对象
     * @return 文件的网络地址
     */
    @Override
    public String uploadFile(MultipartFile file) {
        // 获取原始文件名
        String filename = file.getOriginalFilename();
        if (!StringUtils.isEmpty(filename)) {
            // 扩展名
            String ext = filename.substring(filename.lastIndexOf("."));
            // 重命名文件
            filename = System.currentTimeMillis() + ext;
        }
        try {
            // 上传
            ossClient.putObject(bucket, filename, file.getInputStream());
            // 返回文件的网络地址
            return domain + filename;
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.E_100106);
        }
    }

}