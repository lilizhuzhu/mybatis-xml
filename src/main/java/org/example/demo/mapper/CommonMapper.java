package org.example.demo.mapper;

import org.example.demo.common.SqlQueryRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
public interface CommonMapper {
    List<LinkedHashMap<String, Object>> sqlQueryByCondition(SqlQueryRequest request);
}
