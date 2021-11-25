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
        return DynamicSql.finaAll();
    }

    @GetMapping("/find/{key}")
    public Object find(@PathVariable String key) {
        return DynamicSql.findByKey(key);
    }

    @GetMapping("/find/{key}/{curd}")
    public Object find(@PathVariable String key, @PathVariable String curd) {
        return DynamicSql.findByKeyAndCurd(key, curd);
    }

    @GetMapping("/find/{key}/{curd}/{id}")
    public String find(@PathVariable String key, @PathVariable String curd, @PathVariable String id) {
        return DynamicSql.findByKeyAndCurdAndId(key, curd, id);
    }


    @PostMapping("/run/{dbCode}/{key}/{curd}/{id}")
    public Object run(@PathVariable String dbCode, @PathVariable String key, @PathVariable String curd, @PathVariable String id, @RequestBody Map<String, Object> map) {
        DbCodeEnum dbCodeEnum = DbCodeEnum.getEnumByName(dbCode);
        if (dbCodeEnum == null) {
            return "dbCode错误";
        }
        String sql = DynamicSql.dynamicParseAndAssignmentSqlByStorage(key, curd, id, map);
        if (StringUtils.isNotBlank(sql)) {
            SqlQueryRequest sqlQueryRequest = new SqlQueryRequest();
            sqlQueryRequest.setSql(sql);
            sqlQueryRequest.setUseDbCode(dbCodeEnum);
            return sqlQueryRequest.queryExecute();
        }
        return "sql 有误";
    }
}
