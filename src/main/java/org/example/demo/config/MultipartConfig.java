package org.example.demo.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
@Configuration
public class MultipartConfig {

    @Bean(name = "multipartResolver")
    @ConditionalOnMissingBean(CommonsMultipartResolver.class)
    @ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", matchIfMissing = true)
    public CommonsMultipartResolver multipartResolver() {
        MultipartProperties multipartProperties = SpringUtil.getBean(MultipartProperties.class);
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(multipartProperties.isResolveLazily());
        //限制 上传 总大小 （以字节为单位）
        resolver.setMaxUploadSize(multipartProperties.getMaxRequestSize().toBytes());
        //限制 单独文件大小（以字节为单位）
        resolver.setMaxUploadSizePerFile(multipartProperties.getMaxFileSize().toBytes());
        //在将上传写入磁盘之前设置允许的最大大小（以字节为单位）
        resolver.setMaxInMemorySize(Math.toIntExact(multipartProperties.getFileSizeThreshold().toBytes()));
        return resolver;
    }

    @Bean(name = "multipartResolver")
    @ConditionalOnMissingBean(CommonsMultipartResolver.class)
    @ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", havingValue = "false")
    public CommonsMultipartResolver multipartResolverDefault() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }


}
