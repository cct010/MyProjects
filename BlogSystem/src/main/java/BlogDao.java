import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-07-09
 * Time: 17:36
 */
public class BlogDao {
//    根据博客id来查询指定博客(博客详情页中)
    public Blog selectById(int blogId){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet =null;
        try {
            connection = DBUtil.getConnection();

            String sql = "select * from blog where blogId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1,blogId);

            resultSet =statement.executeQuery();

//            遍历结果合集,由于blogId在blog表重视唯一的(主键),所以只可能有一条或没有
            if(resultSet.next()){
                Blog blog = new Blog();
                blog.setBlogId(resultSet.getInt("blogId"));
                blog.setTitle(resultSet.getString("title"));
                blog.setContent(resultSet.getString("content"));
                blog.setPostTime(resultSet.getTimestamp("postTime"));
                blog.setUserId(resultSet.getInt("userId"));
                return blog;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

//    直接查询出数据库所有的博客列表(博客列表页中)
    public List<Blog> selectAll (){
        List<Blog> blogs = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "select * from blog order by postTime desc";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                Blog blog = new Blog();
                blog.setBlogId(resultSet.getInt("blogId"));
                blog.setTitle(resultSet.getString("title"));
//                在博客列表页中不需要把所有正文显示出来
                String content = resultSet.getString("content");
                if (content == null){
                    content = "";
                }
                if (content.length() >= 100){
                    content = content.substring(0,100) + "...";
                }
                blog.setContent(content);

                blog.setPostTime(resultSet.getTimestamp("postTime"));
                blog.setUserId(resultSet.getInt("userId"));

                blogs.add(blog);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return blogs;
    }

//    添加新博客
    public void add(Blog blog) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBUtil.getConnection();

            String sql = "insert into blog values(null, ? ,? , ? , ? )";
            statement = connection.prepareStatement(sql);
            statement.setString(1, blog.getTitle());
            statement.setString(2, blog.getContent());
            statement.setTimestamp(3,blog.getPostTimestamp());
            statement.setInt(4,blog.getUserId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }

//    删除指定博客
    public void delete(int blogId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtil.getConnection();

            String sql = "delete from blog where blogId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1,blogId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }
}
