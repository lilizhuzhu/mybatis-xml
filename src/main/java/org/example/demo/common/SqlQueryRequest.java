package org.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/20
 */
@Data
public class SqlQueryRequest {
    /**
     * 设置sql
     */
    private String sql;

    /**
     * 使用的数据库编码
     */
    private String useDbCode;

    /**
     * 设置分页
     */
    private Integer pageStart = 0;

    private Integer pageSize = 500;
}
