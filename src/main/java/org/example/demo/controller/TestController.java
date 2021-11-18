package org.example.demo.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.config.nacos.ALLSQL;
import org.example.demo.util.MyBatisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/12
 */
@RestController
public class TestController {


    @GetMapping("findAll")
    public Object findAll() {
        return ALLSQL.finaAll();
    }

    @GetMapping("/find/{key}")
    public Object find(@PathVariable String key){
        String catEyeAbnormalOrderManageExportAdbSqlFor363000 = ALLSQL.findByKey(key);
        return catEyeAbnormalOrderManageExportAdbSqlFor363000;
    }

    @PostMapping("/run/{key}")
    public Object run(@PathVariable String key, @RequestBody Map<String,Object> map){
        String sql = ALLSQL.findByKey(key);
        if (StringUtils.isNotBlank(sql)){
           return  MyBatisUtil.parseDynamicXMLFormXmlStr(sql,map);
        }
        return sql;
    }
}
