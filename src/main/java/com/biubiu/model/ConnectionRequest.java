package com.biubiu.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
@Data
@ApiModel("连接请求实体")
public class ConnectionRequest {

    @ApiModelProperty("用户编码")
    @NotEmpty(message = "用户编码不能为空")
    private String userCode;

    @ApiModelProperty("数据库类型")
    @NotEmpty(message = "数据库类型不能为空")
    private String type;

    @ApiModelProperty("数据库连接地址")
    @NotEmpty(message = "数据库连接地址不能为空")
    private String url;

    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("密码")
    @NotNull(message = "密码不能为空")
    private String password;

}
