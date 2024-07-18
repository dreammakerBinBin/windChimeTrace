CREATE TABLE `wind_chime_trace_config` (
  `id` varchar(32) NOT NULL,
  `config_method` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '配置方法',
  `is_param_enable` varchar(4) DEFAULT NULL COMMENT '入参是否开启',
  `is_output_enable` varchar(255) DEFAULT NULL COMMENT '返回参数是否开启',
  `enable_module` varchar(50) DEFAULT NULL COMMENT '应用模块',
  `is_enable` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置';

