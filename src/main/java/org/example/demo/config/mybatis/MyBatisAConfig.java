package org.example.demo.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.example.demo.interceptor.SqlInterceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
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
@MapperScan(basePackages = MyBatisAConfig.TYPE_ALIASES_PACKAGE, sqlSessionFactoryRef = MyBatisAConfig.SQL_SESSION_FACTORY_NAME)
public class MyBatisAConfig {

    private final static String DATA_BASE_NAME = "mysql_mybatis_a";
    protected final static String TYPE_ALIASES_PACKAGE = "org.example.demo.mapper.a";
    private final static String MAPPER_XML_LOCATIONS = "classpath*:org/example/demo/mapper/a/xml/*Mapper.xml";
    private final static String DATA_SOURCE_NAME = DATA_BASE_NAME + "_DATA_SOURCE_NAME";
    protected final static String SQL_SESSION_FACTORY_NAME = DATA_BASE_NAME + "_SQL_SESSION_FACTORY_NAME";
    private final static String SQL_SESSION_TEMPLATE_NAME = DATA_BASE_NAME + "_SQL_SESSION_TEMPLATE_NAME";

    private final static String url="jdbc:mysql://localhost:3306/mybatis_a?characterEncoding=UTF-8";
    private final static String username="root";
    private final static String password="root_1234";
    private final static String DRIVER_CLASS_NAME= Driver.class.getName();

    @Bean(name = DATA_SOURCE_NAME, initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);


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

        log.info("mapper xml 加载中 {} ...", DATA_SOURCE_NAME);
        factoryBean.setMapperLocations(mapperXmlLocation);
        Arrays.stream(mapperXmlLocation).forEach(r -> log.info("{} 加载完成 {}", DATA_SOURCE_NAME, r));
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