package org.example.demo.config.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/17
 */
@Component
@Slf4j
public class NacosConfig {

    @NacosInjected
    private ConfigService configService;

    @PostConstruct
    public void init() throws Exception {
        log.info("加载 nacos sql 中...");
        // initConfig("catEyeAbnormalOrderManageExportAdbSqlFor363000");
        // initConfig("catEyeAbnormalOrderManageExportAdbSqlNew");
        //  initConfig("catEyeAbnormalOrderManageExportAdbSqlNewOrder");
        // initConfig("catEyeAbnormalOrderManageExportAdbSqlNewOrderFor363000");
        initConfig("testSql", "DEFAULT_GROUP");
        initConfig("xml-catEyeAbnormalOrderManageExportAdbSqlNewOrderFor363000", "DEFAULT_GROUP");
        log.info("nacos sql 全部加载完成");
    }

    public void initConfig(String dataId) throws Exception {
        String group = "xml.sql";
        initConfig(dataId, group);
    }

    public void initConfig(String dataId, String group) throws Exception {
        String dataXML = configService.getConfig(dataId, group, 5000);
        ALLSQL.addSql(dataId, dataXML);
        configService.addListener(dataId, group, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                ALLSQL.addSql(dataId, configInfo);
            }
        });
    }
}
