# 动态日志追踪组件

**为了解决日志追踪问题，且拦截方法可数据库持久化、无需重新发布动态配置化，开发当前动态追踪日志组件。**



- pom依赖引入jar依赖

<dependency>
    <groupId>com.windchime.boot</groupId>
    <artifactId>trace-core</artifactId>
    <version>1.0.1</version>
</dependency>

- 启动类上加入

  ```java
  @Import(WindChimeTracerEntryConfiguration.class)
  ```

- 配置trace-config表，resource目录下有sql文件

  日志配置表：wind_chime_trace_config.sql

  日志记录表（可结合自己项目进行日志存储自定义，当前给出示例表进行日志记录）：wind_chime_trace_service_log.sql

- 在application.properties配置文件中配置 windchime.trace.packag属性；加入日志追踪需要扫描到的包路径

  注意：如果使用了mybatisPlus，需要去除这个包，否则会重复记录日志。

  ```properties
  #默认扫描的包
  windchime.trace.package=com.myself.controller,com.myself.impl,com.myself.mapper,!com.baomidou.mybatisplus
  ```



- 增加java类 继承AbstracttWindChimeTraceServicePersistence.java类，自定义日志存储实体，例如存储到wind_chime_trace_service_log表。

  1. 重写doSaveWindChimeTraceInfo方法实现日志存储到数据库（示例发送给mq处理）
  2. 重写refreshTraceConfig，获取wind_chime_trace_config自定义配置集合

  下面是针对示例wind_chime_trace_service_log表的示例继承类

```java

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class PreApiTraceServicePersistence extends AbstracttWindChimeTraceServicePersistence {

    private final String ENABLE = "1";
    @Autowired
    private IWindChimeTraceConfigService iWindChimeTraceConfigService;
    @Autowired
    private ApiTraceLogProducer rabbitProducer;

    @Value("${spring.application.name}")
    private String moduleName;
    /**
     * 保存tracelog的实现
     */
    @Override
    protected void doSaveWindChimeTraceInfo(WindChimeTraceInfo windChimeTraceInfo, String headersJsonVal) {
        List<WindChimeTraceConfigPojo> windChimeTraceConfigPojos = SingleTraceConfig.getTraceConfigMap();
        windChimeTraceConfigPojos.forEach(config->{
            WindChimeTraceServiceLog windChimeTraceServiceLog = windChimeTraceInfo.getServiceName().equals(config.getConfigMethod())
                    && moduleName.equals(config.getEnableModule())
                    && ENABLE.equals(config.getIsEnable())
                    ? WindChimeTraceServiceLog.builder()
                    .createTime(new Date())
                    .funModule(moduleName)
                    .operateMethod(windChimeTraceInfo.getServiceName())
                    .operateParams(ENABLE.equals(config.getIsParamEnable())?handleInput(windChimeTraceInfo.getArguments()):null)
                    .result(ENABLE.endsWith(config.getIsOutputEnable())?handleOutput(windChimeTraceInfo.getResult()):null)
                    .operateIp(windChimeTraceInfo.getRequestIp())
                    .errorInfo(headersJsonVal)
                    .build()
                    : null;
            if(windChimeTraceServiceLog == null){
                return;
            }
            rabbitProducer.sendApiTraceLogMsg(JSONObject.toJSONString(windChimeTraceServiceLog));
        });
    }

    /**
     * 每3分钟更新一次配置
     */
    @Override
    @Scheduled(fixedRate = 180000, initialDelay = 5000)
    public void refreshTraceConfig() {
        log.info("[windchime-trace][refreshTraceConfig]refreshTraceConfig-apiApplication");
        List<WindChimeTraceConfig> windChimeTraceConfigs = iWindChimeTraceConfigService.list();
        List<WindChimeTraceConfigPojo> traceConfigExtends = BeanUtil.copyToList(windChimeTraceConfigs, WindChimeTraceConfigPojo.class);
        SingleTraceConfig.setTraceConfigCache(traceConfigExtends);
    }
}

```





【1】在wind_chime_trace_config表中进行配置对应的方法 具体到方法名，具体配置可以参照下里面已有的配置，1为启用，可分别配置入参出参是否记录
【2】表 wind_chime_trace_service_log：具体的执行日志记录
【3】没在wind_chime_trace_config配置的方法不会进行日志追踪







