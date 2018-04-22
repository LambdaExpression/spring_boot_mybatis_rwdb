package org.tcat.parent.mybaits.intercepts;


import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tcat.parent.mybaits.dataSource.DynamicDataSourceHolder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Batis拦截器。
 * <br/>对 SimpleExecutor.doQuery(..) 的拦截可以参考一下 https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/Interceptor.md
 */
@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class RWIntercepts implements Interceptor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder()
                    .append("\n\n\n\n\n")
                    .append("\n##############################RWIntercepts##############################")
                    .append("\n")
                    .append("\nclass            =   ").append(ms.getId())
                    .append("\nresource         =   ").append(ms.getResource())
                    .append("\nsqlCommandType   =   ").append(ms.getSqlCommandType())
                    .append("\n")
                    .append("\n########################################################################")
                    .append("\n\n\n\n\n");
            logger.error(sb.toString());
        }
        try {
            if (ms.getSqlCommandType() == SqlCommandType.SELECT) {
                DynamicDataSourceHolder.markSlave();
            }
            return invocation.proceed();
        } catch (Exception e) {
            logger.error("数据源读写分离路由异常!\n{}", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("==============NOT EnableRWDB=============");
        }
        DynamicDataSourceHolder.markMaster();
        return invocation.proceed();
    }

    private Object getFieldValue(Object object, String name) throws IllegalAccessException {
        if (object == null || name == null || "".equals(name.trim())) {
            return null;
        }
        Class<?> obj = object.getClass();
        Set<Field> fieldList = new HashSet<>();
        while (obj != null) {
            for (Field field : fieldList) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    return field.get(object);
                }
            }
            fieldList.addAll(Arrays.asList(obj.getDeclaredFields()));
            obj = obj.getSuperclass();
        }
        return null;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // NOOP
    }

}
