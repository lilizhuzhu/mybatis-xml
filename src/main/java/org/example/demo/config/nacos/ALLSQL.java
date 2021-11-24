package org.example.demo.config.nacos;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.demo.util.MyBatisUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/17
 */
@Slf4j
public class ALLSQL {

    private static final Map<String, Map<String, Map<String, String>>> sqlMap = new ConcurrentHashMap<>();

    public static void addSql(String key, String allXmlSql) {
        log.info("尝试加载 nacos sql dataId={} ", key);
        if (StringUtils.isNoneBlank(key, allXmlSql)) {
            Map<String, Map<String, String>> stringMapMap = MyBatisUtil.selectParseXML(allXmlSql);
            if (MapUtil.isNotEmpty(stringMapMap)) {
                sqlMap.put(key, stringMapMap);
                log.info("加载成功 nacos sql sqlMapper={} ", JSON.toJSONString(stringMapMap));
            }
        }
    }

    public static String findByKey(String key, String curd, String id) {
        Map<String, Map<String, String>> stringMapMap = sqlMap.get(key);
        if (MapUtil.isNotEmpty(stringMapMap)) {
            Map<String, String> map = stringMapMap.get(curd);
            if (MapUtil.isNotEmpty(map)) {
                return map.get(id);
            }
        }
        return null;
    }

    public static Map<String, Map<String, String>> findByKey(String key) {
        Map<String, Map<String, String>> stringMapMap = sqlMap.get(key);
        if (MapUtil.isNotEmpty(stringMapMap)) {
            return JSON.parseObject(JSON.toJSONString(stringMapMap), new TypeReference<Map<String, Map<String, String>>>() {});
        }
        return null;
    }

    public static Map<String, String> findByKey(String key, String curd) {
        Map<String, Map<String, String>> stringMapMap = sqlMap.get(key);
        if (MapUtil.isNotEmpty(stringMapMap)) {
            Map<String, String> map = stringMapMap.get(curd);
            if (MapUtil.isNotEmpty(map)) {
                return JSON.parseObject(JSON.toJSONString(map), new TypeReference<Map<String, String>>() {});
            }
        }
        return null;
    }

    public static Map<String, Map<String, Map<String, String>>> finaAll() {
        Map<String, Map<String, Map<String, String>>> map = new HashMap<>();
        map.putAll(sqlMap);
        return map;
    }
}
