package org.example.demo.config.nacos;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/17
 */
@Slf4j
public class ALLSQL {

    private static final Map<String,Map<String,String>> sqlMap = new ConcurrentHashMap<>();

    public static void addSql(String key,String allXmlSql){

       /* if (StringUtils.isNoneEmpty(xmlSql,key)){
            sqlMap.put(key,xmlSql);
            log.info("add sql , key:{}, xmlSql:{}",key,xmlSql);
        }*/
    }
    public static String findByKey(String key){
        //return sqlMap.get(key);
        return null;
    }
    public static  Map<String,String> finaAll(){
        return null;
    }
}
