package org.example.demo.service;

import org.example.demo.common.DbCodeEnum;
import org.springframework.stereotype.Service;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
@Service
public class SchoolService implements CommonService {
    @Override
    public DbCodeEnum getDbCodeEnum() {
        return DbCodeEnum.DB_B;
    }



}
