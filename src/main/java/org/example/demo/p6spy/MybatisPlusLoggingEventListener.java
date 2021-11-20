package org.example.demo.p6spy;


import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.logging.LoggingEventListener;

import java.sql.SQLException;

/**
 * 监听事件
 *
 * @author nieqiurong
 * @since 2019-11-10
 */
public class MybatisPlusLoggingEventListener extends LoggingEventListener {
    private static MybatisPlusLoggingEventListener INSTANCE;

    public static MybatisPlusLoggingEventListener getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new MybatisPlusLoggingEventListener();
        }
        return INSTANCE;
    }

    @Override
    public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
        //忽略批量执行结果
    }

}
