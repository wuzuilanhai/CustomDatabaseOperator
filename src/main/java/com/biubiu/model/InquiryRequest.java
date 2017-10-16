package com.biubiu.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by zhanghaibiao on 2017/9/7.
 */
@Data
@ApiModel("查询请求实体")
public class InquiryRequest {

    @ApiModelProperty("用户编码")
    @NotEmpty(message = "用户编码不能为空")
    private String userCode;

    @ApiModelProperty("唯一数据库连接编码")
    @NotEmpty(message = "唯一数据库连接编码不能为空")
    private String uniqueConnectionCode;

    @ApiModelProperty("查询sql语句")
    @NotEmpty(message = "查询sql语句不能为空")
    private String sql;

    @ApiModelProperty("查询参数")
    private Object[] params;

}
