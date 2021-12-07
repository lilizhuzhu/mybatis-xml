package org.example.demo.config.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.example.demo.common.MapperNameSpace;
import org.example.demo.util.MyBatisUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/12/7
 */
public class DynamicSql {
    private static final Map<String, MapperNameSpace> mapperNameSpaceMap = new HashMap<>();

    public static void addOrUpdateOne(MapperNameSpace mapperNameSpace) {
        if (mapperNameSpace != null && StringUtils.isNotBlank(mapperNameSpace.getNamespace())) {
            mapperNameSpaceMap.put(mapperNameSpace.getNamespace(), mapperNameSpace);
        }
    }

    public static void addOrUpdateOne(String dataXML) {
        MapperNameSpace ysMapper = MyBatisUtil.getYSMapper(dataXML);
        if (ysMapper != null) {
            addOrUpdateOne(ysMapper);
        }
    }

    public static String getSqlXmlByNameSpaceAndId(String namespace, String id) {
        MapperNameSpace nameSpace = mapperNameSpaceMap.get(namespace);
        if (nameSpace == null || nameSpace.getIdMapperMap().isEmpty()) {
            return StringUtils.EMPTY;
        }
        return nameSpace.getById(id);
    }

    public static Map<String, MapperNameSpace> findAll() {
        return JSON.parseObject(JSON.toJSONString(mapperNameSpaceMap), new TypeReference<Map<String, MapperNameSpace>>() {
        });
    }
}
