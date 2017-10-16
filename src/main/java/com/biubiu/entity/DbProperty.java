package com.biubiu.entity;

import lombok.Data;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
@Data
public class DbProperty {

    /**
     * 驱动类
     */
    private String driver;

    /**
     * 数据库连接地址
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public DbProperty(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
