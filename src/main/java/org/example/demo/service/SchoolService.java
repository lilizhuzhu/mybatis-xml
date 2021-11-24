package org.example.demo.service;

import org.example.demo.common.DbCode;
import org.example.demo.common.SqlQueryRequest;
import org.example.demo.mapper.a.CommonAMapper;
import org.example.demo.mapper.b.CommonBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
@Service
public class SchoolService {
    @Autowired
    private CommonBMapper commonBMapper;

    public List<LinkedHashMap<String, Object>> sqlQueryByCondition(SqlQueryRequest request){
        if (request==null){
            return null;
        }
        request.setUseDbCode(DbCode.DB_B);
        List<LinkedHashMap<String, Object>> linkedHashMaps = commonBMapper.sqlQueryByCondition(request);
        return linkedHashMaps;
    }
}
