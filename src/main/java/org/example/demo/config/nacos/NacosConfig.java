package org.example.demo.config.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.demo.common.MapperNameSpace;
import org.example.demo.util.MyBatisUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
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
        newInitConfig("mybatis_a.a_student", "mysql.mybatis_a");
        newInitConfig("mybatis_b.b_school", "mysql.mybatis_b");
        log.info("nacos sql 全部加载完成");
    }

    public void newInitConfig(String dataId, String group) {
        String dataXML = null;
        try {
            dataXML = configService.getConfig(dataId, group, 5000);
            NewDynamicSql.addOrUpdateOne(dataXML);
            configService.addListener(dataId, group, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    NewDynamicSql.addOrUpdateOne(configInfo);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("nacos 文件加载失败 ,dataId="+dataId+",group="+group+",value="+dataXML+"\n,err="+e.getMessage());
        }


    }
}
