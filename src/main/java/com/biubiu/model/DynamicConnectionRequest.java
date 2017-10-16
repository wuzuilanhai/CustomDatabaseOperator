package com.biubiu.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by zhanghaibiao on 2017/9/8.
 */
@Data
@ApiModel("动态连接请求实体")
public class DynamicConnectionRequest extends ConnectionRequest{

    @ApiModelProperty("驱动URL")
    @NotEmpty(message = "驱动URL不能为空")
    private String jarUrl;

}
