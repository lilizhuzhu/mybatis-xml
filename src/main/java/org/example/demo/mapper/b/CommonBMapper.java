package org.example.demo.mapper.b;

import org.apache.ibatis.annotations.Mapper;
import org.example.demo.common.SqlQueryRequest;

import java.util.List;
import java.util.LinkedHashMap;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/23
 */
@Mapper
public interface CommonBMapper {
    List<LinkedHashMap<String, Object>> sqlQueryByCondition(SqlQueryRequest request);
}
