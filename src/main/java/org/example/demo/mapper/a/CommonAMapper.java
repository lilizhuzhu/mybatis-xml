package org.example.demo.mapper.a;

import org.apache.ibatis.annotations.Mapper;
import org.example.demo.common.SqlQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/20
 */
@Mapper
public interface CommonAMapper {
    List<Map<String, Object>> sqlQueryByCondition(SqlQueryRequest request);
}
