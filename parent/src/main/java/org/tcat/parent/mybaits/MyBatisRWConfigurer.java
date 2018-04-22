package org.tcat.parent.mybaits;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ObjectUtils;
import org.tcat.parent.mybaits.dataSource.DynamicDataSource;
import org.tcat.parent.mybaits.intercepts.RWIntercepts;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by Lin on 2017/4/19.
 */
@MapperScan("**.domain.**")
@Configuration
@EnableTransactionManagement
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableConfigurationProperties(MybatisProperties.class)
@ConfigurationProperties("spring.datasource")
public class MyBatisRWConfigurer {

    @Autowired
    private MybatisProperties properties;
    @Autowired(required = false)
    private Interceptor[] interceptors;

    @Bean
    @ConfigurationProperties("spring.datasource.write")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    List<Map<String, String>> read = new ArrayList<>();

    public List<Map<String, String>> getRead() {
        return read;
    }

    public MyBatisRWConfigurer setRead(List<Map<String, String>> read) {
        this.read = read;
        return this;
    }

    @Bean
    public List<DataSource> getReadDataSources() {
        List<DataSource> dataSourceList = new ArrayList<>();
        if (read != null && read.size() != 0) {
            for (Map<String, String> map : read) {
                dataSourceList.add(DataSourceBuilder.create()
                        .url(map.get("url"))
                        .username(map.get("username"))
                        .password(map.get("password"))
                        .build());
            }
        }
        return dataSourceList;
    }

    @Bean(name = "masterHikariDataSource")
    public HikariDataSource masterHikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSource(masterDataSource());
        return new HikariDataSource(config);
    }


    @Bean(name = "slaveHikariDataSource")
    public HikariDataSource slaveHikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSource(getReadDataSources().get(0));
        return new HikariDataSource(config);
    }

    @Bean
    public DynamicDataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> data = new HashMap<>(4);
        data.put("master", masterHikariDataSource());
        data.put("slave", slaveHikariDataSource());
        dynamicDataSource.setTargetDataSources(data);
        dynamicDataSource.setDefaultTargetDataSource(masterHikariDataSource());
        return dynamicDataSource;
    }


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DynamicDataSource dataSource) {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        Interceptor rwplugin = new RWIntercepts();
        if (ObjectUtils.isEmpty(this.interceptors)) {
            Interceptor[] plugins = {rwplugin};
            factory.setPlugins(plugins);
        } else {
            List<Interceptor> interceptorList = Arrays.asList(interceptors);
            interceptorList.add(rwplugin);
            factory.setPlugins((Interceptor[]) interceptorList.toArray());
        }
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage("**.entity");
        factory.setConfigLocation(new DefaultResourceLoader().getResource("classpath:/mybatis-configuration.xml"));
        try {
            return factory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

}
