package com.biubiu.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
@Data
@ApiModel("查询数据表请求实体")
public class ListTablesRequest {

    @ApiModelProperty("用户编码")
    @NotEmpty(message = "用户编码不能为空")
    private String userCode;

    @ApiModelProperty("唯一数据库连接编码")
    @NotEmpty(message = "唯一数据库连接编码不能为空")
    private String uniqueConnectionCode;

}
