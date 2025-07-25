package com.huiminpay.model;



import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderExt implements Serializable {

    private String id;

    private Date createTime;

    private Long money;

    private String title;

}