package pers.ocean.shardingspherejdbcdemo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static pers.ocean.shardingspherejdbcdemo.util.DataSourceUtils.getDataSource;

@SpringBootApplication
public class ShardingsphereJdbcDemoApplication {

    public static void main(String[] args) throws Exception {
        //SpringApplication.run(ShardingsphereJdbcDemoApplication.class, args);
        // 测试分库分表
        testAdd();
    }

    private static void testAdd() throws SQLException {
        Connection conn = getDataSource().getConnection();

        for (int i = 1; i < 21; i++) {
            int user_id = RandomUtils.nextInt(1, 60);
            String sql = "insert into t_order(order_id, user_id, status) values(" + i + "," + user_id + "," + "'状态" + i
                + "'" + ") ";
            System.out.println(sql);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            boolean execute = preparedStatement.execute();
        }
    }

}
