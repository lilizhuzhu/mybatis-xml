package org.example.demo.service;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.common.DbCodeEnum;
import org.example.demo.common.SqlQueryRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
public interface CommonService {

    DbCodeEnum getDbCodeEnum();
    default List<LinkedHashMap<String, Object>> sqlQueryByCondition(SqlQueryRequest request) {
        if (request != null || StringUtils.isBlank(request.getSql()) || getDbCodeEnum() == null) {
            return null;
        }
        return getDbCodeEnum().getCommonMapper().sqlQueryByCondition(request);
    }
}
