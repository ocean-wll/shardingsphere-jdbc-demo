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

        ShardingTableRuleConfiguration orderTableRuleConfig = new ShardingTableRuleConfiguration("t_order",
            "demo_ds_${0..1}.t_order_${0..1}");

        // 配置分库策略
        orderTableRuleConfig.setDatabaseShardingStrategy(
            new StandardShardingStrategyConfiguration("user_id", "dbShardingAlgorithm"));

        // 配置分表策略
        orderTableRuleConfig.setTableShardingStrategy(
            new StandardShardingStrategyConfiguration("order_id", "tableShardingAlgorithm"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTables().add(orderTableRuleConfig);

        // 配置分库算法
        Properties dbShardingAlgorithmrProps = new Properties();
        dbShardingAlgorithmrProps.setProperty("algorithm-expression", "demo_ds_${user_id % 2}");
        shardingRuleConfig.getShardingAlgorithms().put("dbShardingAlgorithm",
            new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmrProps));

        // 配置分表算法
        Properties tableShardingAlgorithmrProps = new Properties();
        tableShardingAlgorithmrProps.setProperty("algorithm-expression", "t_order_${order_id % 2}");
        shardingRuleConfig.getShardingAlgorithms().put("tableShardingAlgorithm",
            new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmrProps));

        // 获取数据源对象
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap,
            Collections.singleton(shardingRuleConfig), new Properties());
    }
}


