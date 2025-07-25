package com.huiminpay.merchant.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件至阿里云OSS
     * @param file 上传的文件
     * @return 文件路径
     */
    public String uploadFile(MultipartFile file);

}
