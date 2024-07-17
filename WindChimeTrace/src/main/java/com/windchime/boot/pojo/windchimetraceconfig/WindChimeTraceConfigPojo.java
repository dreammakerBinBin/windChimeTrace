package com.windchime.boot.pojo.windchimetraceconfig;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
//@TableName("wind_chime_trace_config")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WindChimeTraceConfigPojo implements Serializable {

//    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

//    @TableField("config_method")
    private String configMethod;

//    @TableField("is_param_enable")
    private String isParamEnable;

//    @TableField("is_output_enable")
    private String isOutputEnable;

//    @TableField("enable_module")
    private String enableModule;

//    @TableField("is_enable")
    private String isEnable;
}