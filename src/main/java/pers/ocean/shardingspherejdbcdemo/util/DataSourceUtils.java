package pers.ocean.shardingspherejdbcdemo.util;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/23 10:55 上午
 */
public class DataSourceUtils {

    public static DataSource getDataSource() throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置第一个数据源
        BasicDataSource dataSource1 = new BasicDataSource();
        dataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource1.setUrl("jdbc:mysql://127.0.0.1:3306/demo_ds_0");
        dataSource1.setUsername("root");
        dataSource1.setPassword("");
        dataSourceMap.put("demo_ds_0", dataSource1);

        // 配置第二个数据源
        BasicDataSource dataSource2 = new BasicDataSource();
        dataSource2.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource2.setUrl("jdbc:mysql://127.0.0.1:3306/demo_ds_1");
        dataSource2.setUsername("root");
        dataSource1.setPassword("");
        dataSourceMap.put("demo_ds_1", dataSource2);

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        // 对t_order表进行分库分表
        shardingRuleConfig.getTables().add(new ShardingTableRuleConfiguration("t_order"));
        shardingRuleConfig.getBindingTableGroups().add("t_order");

        // 广播表：暂时不知道是什么用
        //shardingRuleConfig.getBroadcastTables().add("t_address");
        // 分库规则
        shardingRuleConfig.setDefaultDatabaseShardingStrategy(
            new StandardShardingStrategyConfiguration("user_id", "db_inline"));
        Properties props = new Properties();
        props.setProperty("algorithm-expression", "demo_ds_${user_id % 2}");
        shardingRuleConfig.getShardingAlgorithms().put("db_inline",
            new ShardingSphereAlgorithmConfiguration("INLINE", props));

        Properties propertie = new Properties();
        //是否打印SQL解析和改写日志
        propertie.setProperty("sql.show", Boolean.TRUE.toString());
        // 获取数据源对象
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap,
            Collections.singleton(shardingRuleConfig), propertie);
    }
}


