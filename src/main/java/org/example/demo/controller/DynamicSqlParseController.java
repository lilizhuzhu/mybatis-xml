package org.example.demo.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.common.DbCodeEnum;
import org.example.demo.common.SqlQueryRequest;
import org.example.demo.config.nacos.DynamicSql;
import org.example.demo.util.MyBatisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/12
 */
@RestController
@RequestMapping("/dynamic/sql")
public class DynamicSqlParseController {

    @GetMapping("findAll")
    public Object findAll() {
        return DynamicSql.findAll();
    }

    @GetMapping("/find/{namespace}/{id}")
    public String find(@PathVariable String namespace, @PathVariable String id) {
        return DynamicSql.getSqlXmlByNameSpaceAndId(namespace, id);
    }
    @PostMapping("/run/{dbCode}/{namespace}/{id}")
    public Object run(@PathVariable String dbCode, @PathVariable String namespace, @PathVariable String id, @RequestBody Map<String, Object> map) {
        String sql = DynamicSql.getSqlXmlByNameSpaceAndId(namespace, id);
        if (StringUtils.isNotBlank(sql)) {
            SqlQueryRequest sqlQueryRequest = new SqlQueryRequest();
            sqlQueryRequest.setSql(MyBatisUtil.parseDynamicXMLFormXmlStr(sql, map));
            DbCodeEnum dbCodeEnum = DbCodeEnum.getEnumByName(dbCode);
            if (dbCodeEnum==null){
                return "dbCode错误";
            }
            return dbCodeEnum.getCommonMapper().sqlQueryByCondition(sqlQueryRequest);
        }
        return sql;
    }
}
