package org.example.demo.common;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.demo.mapper.CommonMapper;
import org.example.demo.mapper.a.CommonAMapper;
import org.example.demo.mapper.b.CommonBMapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
@Getter
@AllArgsConstructor
public enum DbCodeEnum {

    DB_A(CommonAMapper.class), DB_B(CommonBMapper.class);

    private static Set<DbCodeEnum> allDbCodeEnum = new HashSet<DbCodeEnum>() {{
        add(DB_A);
        add(DB_B);
    }};

    private Class<? extends CommonMapper> commonMapperClass;
    private String dbCode;
    private String details;

    public <T extends CommonMapper> CommonMapper getCommonMapper() {
        return SpringUtil.getBean(commonMapperClass);
    }

    public static DbCodeEnum getEnumByName(String name) {
        return allDbCodeEnum.stream().filter(s -> s.name().equals(name)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    DbCodeEnum(Class<? extends CommonMapper> commonMapperClass) {
        this.commonMapperClass = commonMapperClass;
        this.dbCode = "";
        this.details = "";
    }


}
