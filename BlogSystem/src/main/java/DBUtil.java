import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-07-09
 * Time: 15:48
 */

//此文件是编写数据库连接操作
public class DBUtil {
    private static DataSource dataSource = new MysqlDataSource();

    static {
        ((MysqlDataSource) dataSource).setUrl("jdbc:mysql://127.0.0.1:3306/blog_system?characterEncoding=utf8&useSSL=false");
        ((MysqlDataSource) dataSource).setUser("root");
        ((MysqlDataSource) dataSource).setPassword("");
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
//        三个分开写是防止,有一个出错,其他也能正常进行
//        执行顺序从小到大进行关闭资源
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
