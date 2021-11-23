package org.example.demo.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.common.SqlQueryRequest;
import org.example.demo.config.nacos.ALLSQL;
import org.example.demo.mapper.a.CommonAMapper;
import org.example.demo.util.MyBatisUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
   private CommonAMapper commonAMapper;


    @GetMapping("findAll")
    public Object findAll() {
        return ALLSQL.finaAll();
    }

    @PostMapping("/find/{key}/{curd}/{id}")
    public String find(@PathVariable String key,@PathVariable String curd,@PathVariable String id){
        return ALLSQL.findByKey(key,curd,id);
    }

    @PostMapping("/run/{key}/{curd}/{id}")
    public Object run(@PathVariable String key,@PathVariable String curd,@PathVariable String id,@RequestBody Map<String,Object> map){
        String sql = ALLSQL.findByKey(key,curd,id);
        if (StringUtils.isNotBlank(sql)){
            SqlQueryRequest sqlQueryRequest = new SqlQueryRequest();
            sqlQueryRequest.setSql(MyBatisUtil.parseDynamicXMLFormXmlStr(sql, map));
            return commonAMapper.sqlQueryByCondition(sqlQueryRequest);
        }
        return sql;
    }
}
