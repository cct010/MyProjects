import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-07-09
 * Time: 17:36
 */
//根据用户表提供的一些基本操作
//    没有删号功能,也没有注册功能
public class UserDao {

//    根据userId来查询用户信息
    public User selectById (int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
//            和数据库建立连接
            connection = DBUtil.getConnection();
//            构造sql
            String sql = "select * from user where userId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
//            执行sql
            resultSet = statement.executeQuery();
//            遍历结果合集,因为只可能有一个,使用if判断
            if(resultSet.next()){
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

//    根据username来查询用户信息(登录时)
    public User selectByUsername (String username){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
         try {
             connection = DBUtil.getConnection();

             String sql = "select * from user where username = ?";
             statement = connection.prepareStatement(sql);
             statement.setString(1,username);

             resultSet = statement.executeQuery();

             if (resultSet.next()){
                 User user = new User();
                 user.setUserId(resultSet.getInt("userId"));
                 user.setUsername(resultSet.getString("username"));
                 user.setPassword(resultSet.getString("password"));
                 return user;
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }finally {
             DBUtil.close(connection,statement,resultSet);
         }
         return null;
    }
}
