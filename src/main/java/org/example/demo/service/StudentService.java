package org.example.demo.service;

import org.example.demo.common.DbCode;
import org.example.demo.common.SqlQueryRequest;
import org.example.demo.mapper.a.CommonAMapper;
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
public class StudentService {
    @Autowired
    private CommonAMapper commonAMapper;
    public List<LinkedHashMap<String, Object>> sqlQueryByCondition(SqlQueryRequest request){
        if (request==null){
            return null;
        }
        request.setUseDbCode(DbCode.DB_A);
        List<LinkedHashMap<String, Object>> linkedHashMaps = commonAMapper.sqlQueryByCondition(request);
        return linkedHashMaps;
    }
}
