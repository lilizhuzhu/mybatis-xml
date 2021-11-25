package org.example.demo.config.nacos;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.demo.common.DbCodeEnum;
import org.example.demo.common.SqlQueryRequest;
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
public class DynamicSql {

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

    public static Map<String, Map<String, String>> findByKey(String key) {
        if (StringUtils.isAnyBlank(key)) {
            return null;
        }
        Map<String, Map<String, String>> stringMapMap = sqlMap.get(key);
        if (MapUtil.isNotEmpty(stringMapMap)) {
            return JSON.parseObject(JSON.toJSONString(stringMapMap), new TypeReference<Map<String, Map<String, String>>>() {
            });
        }
        return null;
    }

    public static Map<String, String> findByKeyAndCurd(String key, String curd) {
        if (StringUtils.isAnyBlank(key, curd)) {
            return null;
        }
        Map<String, Map<String, String>> stringMapMap = sqlMap.get(key);
        if (MapUtil.isNotEmpty(stringMapMap)) {
            Map<String, String> map = stringMapMap.get(curd);
            if (MapUtil.isNotEmpty(map)) {
                return JSON.parseObject(JSON.toJSONString(map), new TypeReference<Map<String, String>>() {
                });
            }
        }
        return null;
    }

    public static String findByKeyAndCurdAndId(String key, String curd, String id) {
        if (StringUtils.isAnyBlank(key, curd, id)) {
            return null;
        }
        Map<String, Map<String, String>> stringMapMap = sqlMap.get(key);
        if (MapUtil.isNotEmpty(stringMapMap)) {
            Map<String, String> map = stringMapMap.get(curd);
            if (MapUtil.isNotEmpty(map)) {
                return map.get(id);
            }
        }
        return null;
    }

    public static Map<String, Map<String, Map<String, String>>> finaAll() {
        return JSON.parseObject(JSON.toJSONString(sqlMap), new TypeReference<Map<String, Map<String, Map<String, String>>>>() {
        });
    }

    /**
     * 返回解析好的sql
     * @param key
     * @param curd
     * @param id
     * @param parameterObject
     * @return
     */
    public static String dynamicParseAndAssignmentSqlByStorage(String key, String curd, String id, Object parameterObject) {
        String xmlSql = findByKeyAndCurdAndId(key, curd, id);
        if (StringUtils.isAnyBlank(xmlSql)) {
            log.error("没有该sql");
            return null;
        }
       return MyBatisUtil.parseDynamicXMLFormXmlStr(xmlSql,parameterObject);
    }

    public static void  executeSql(SqlQueryRequest sqlQueryRequest){
        DbCodeEnum useDbCode = sqlQueryRequest.getUseDbCode();
        useDbCode.getCommonMapper().sqlQueryByCondition(sqlQueryRequest);

    }
}
