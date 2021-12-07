package org.example.demo.controller;

import org.example.demo.config.nacos.NewDynamicSql;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



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
        return NewDynamicSql.findAll();
    }

    @GetMapping("/find/{namespace}/{id}")
    public String find(@PathVariable String namespace, @PathVariable String id) {
        return NewDynamicSql.getSqlXmlByNameSpaceAndId(namespace, id);
    }

}
