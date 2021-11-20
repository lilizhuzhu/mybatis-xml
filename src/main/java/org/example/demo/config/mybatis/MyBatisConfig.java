package org.example.demo.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/20
 */
@Slf4j
@Configuration
@MapperScan(basePackages = MyBatisConfig.TYPE_ALIASES_PACKAGE,sqlSessionFactoryRef=MyBatisConfig.SQL_SESSION_FACTORY_NAME)
public class MyBatisConfig {

    protected final static String TYPE_ALIASES_PACKAGE = "org.example.demo.mapper.test1";
    private final static String MAPPER_XML_LOCATIONS = "classpath*:org/example/demo/mapper/test1/xml/*Mapper.xml";
    private final static String DATA_SOURCE_NAME = "mysql-test1" + "DATA_SOURCE_NAME";
    protected final static String SQL_SESSION_FACTORY_NAME = "mysql-test1" + "SQL_SESSION_FACTORY_NAME";
    private final static String SQL_SESSION_TEMPLATE_NAME = "mysql-test1" + "SQL_SESSION_TEMPLATE_NAME";

    @Bean(name = DATA_SOURCE_NAME, initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        //dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setDriverClassName(com.p6spy.engine.spy.P6SpyDriver.class.getName());
        dataSource.setUrl("jdbc:p6spy:mysql://localhost:3306/test1?characterEncoding=UTF-8");
        dataSource.setUsername("root");
        dataSource.setPassword("root_1234");


        dataSource.setInitialSize(5);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);

        dataSource.setTimeBetweenEvictionRunsMillis(2000);
        dataSource.setMinEvictableIdleTimeMillis(600000);
        dataSource.setMaxEvictableIdleTimeMillis(900000);
        //在空闲时检查有效性, 默认false
        dataSource.setTestWhileIdle(true);

        dataSource.setValidationQuery("SELECT 1");
        //在获取连接的时候检查有效性, 默认false
        dataSource.setTestOnBorrow(false);
        //在连接对象返回时，是否测试对象的有效性,默认false
        dataSource.setTestOnReturn(false);
        dataSource.setKeepAlive(true);
        dataSource.setPhyMaxUseCount(1000);
        dataSource.setFilters("stat");

        return dataSource;
    }

    @Bean(name = SQL_SESSION_FACTORY_NAME)
    public SqlSessionFactory sqlSessionFactory(@Qualifier(DATA_SOURCE_NAME) DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        Resource[] mapperXmlLocation = new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_LOCATIONS);

        log.error("{} MAPPER_XML_LOCATIONS 加载中 ...",DATA_SOURCE_NAME);
        factoryBean.setMapperLocations(mapperXmlLocation);
        log.error("{} MAPPER_XML_LOCATIONS 加载完成",DATA_SOURCE_NAME);
        Arrays.stream(mapperXmlLocation).forEach(r->log.error("{}  加载完成 ",r));
        factoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        factoryBean.getObject().getConfiguration().addInterceptor(new SqlInterceptor());
        factoryBean.getObject().getConfiguration().setCallSettersOnNulls(true);
        return factoryBean.getObject();
    }

    @Bean(name = SQL_SESSION_TEMPLATE_NAME)
    public SqlSessionTemplate buildSqlSessionTemplate(
            @Qualifier(SQL_SESSION_FACTORY_NAME) SqlSessionFactory adbASqlSessionFactory) {
        return new SqlSessionTemplate(adbASqlSessionFactory);
    }

}