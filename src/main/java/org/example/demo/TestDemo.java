package org.example.demo;

import org.example.demo.util.MyBatisUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/12
 */
public class TestDemo {




    @Test
    public void dd() {
        String sql1 = " <select id=\"dd\">\n" +
                "        select id,name from oss_file\n" +
                "        <where>\n" +
                "            <if test=\"url==1\">\n" +
                "                and id = 1\n" +
                "            </if>\n" +
                "            <if test=\"url!=1\">\n" +
                "                and name = #{people.name,jdbcType=VARCHAR}\n" +
                "                and age = #{people.age}\n" +
                "            </if>\n" +
                "            <if test=\"url!=1\">\n" +
                "                and id in <foreach collection=\"idList\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n" +
                "                #{item}\n" +
                "            </foreach>\n" +
                "            </if> group by id\n" +
                "        </where>\n" +
                "    </select>";

        Map<String, Object> map1 = new HashMap<String, Object>() {
            {
                put("url", 3);
                put("uop", "李四");
                put("idList", Arrays.asList(2, 3, 4, 5, 6));
                put("people", new HashMap<String, Object>() {
                    {
                        put("name", "刘备");
                        put("age", 22);
                    }
                });
            }
        };
        System.out.println(sql1);
        System.out.println("-----------");
        System.out.println(MyBatisUtil.parseDynamicXMLFormXmlStr(sql1,map1));
    }
}
