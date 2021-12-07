package org.example.demo.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/12/7
 */

public class MapperNameSpace {
    @Setter
    @Getter
    private String namespace;
    @Setter
    @Getter
    private Map<IdLabelType, Map<String, String>> idMapperMap = Maps.newHashMap();

    public void putIdXml(IdLabelType idLabelType, String id, String xml) {
        if (idLabelType == null || StringUtils.isAnyBlank(id, xml)) {
            return;
        }
        Map<String, String> idMappers = idMapperMap.get(idLabelType);
        if (idMappers == null) {
            idMappers = Maps.newHashMap();
            idMapperMap.put(idLabelType, idMappers);
        }
        idMappers.put(id, xml);
    }

    public void removeById(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        Collection<Map<String, String>> values = idMapperMap.values();
        values.forEach(v -> {
            v.remove(id);
        });
    }

    public Map<IdLabelType, Map<String, String>> getIdMapperMap() {
        return JSON.parseObject(JSON.toJSONString(idMapperMap), new TypeReference<Map<IdLabelType, Map<String, String>>>() {
        });
    }

    public String getById(String id) {
       return idMapperMap.values().stream().map(im -> {
            return im.get(id);
        }).filter(StringUtils::isNotBlank).findFirst().orElse(null);
    }
}
