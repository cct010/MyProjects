import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-07-10
 * Time: 14:32
 */
@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
//    从服务器获取数据
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogDao blogDao = new BlogDao();
        String blogId = req.getParameter("blogId");
        if (blogId == null){
//           querystring不存在说明是获取博客列表
            List<Blog> blogs = blogDao.selectAll();
//        把blog转成符合要求的json格式字符串
            String respJson = objectMapper.writeValueAsString(blogs);
//        显示告诉浏览器,数据是json格式,字符集是utf8
            resp.setContentType("application/json; charset=utf8");
//        把这个字符串写回响应body中
            resp.getWriter().write(respJson);
        } else{
            Blog blog = blogDao.selectById(Integer.parseInt(blogId));
            if(blog == null){
                System.out.println("当前blogId = " + blogId + "对应的博客不存在!");
            }
            String respJson = objectMapper.writeValueAsString(blog);
            resp.setContentType("application/json; charset=utf8");
            resp.getWriter().write(respJson);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        读取请求,构造Blog对象,插入数据库中即可
        HttpSession httpSession = req.getSession(false);
        if (httpSession ==null){
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前未登录,无法发布博客!");
            return;
        }
        User user = (User) httpSession.getAttribute("user");
        if(user == null){
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前未登录,无法发布博客!");
            return;
        }
//        获取博客标题和正文
//        指定请求的字符集,告诉servlet按照哪种字符集来解析请求,后续提交数据库的时候才不会乱码
        req.setCharacterEncoding("utf8");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        if(title == null || "".equals(title) || content==null || "".equals(content)){
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前提交数据有误!标题或正文为空!");
            return;
        }
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setUserId(user.getUserId());
        blog.setPostTime(new Timestamp(System.currentTimeMillis()));
//        插入数据库
        BlogDao blogDao = new BlogDao();
        blogDao.add(blog);
//        跳转到博客列表页
        resp.sendRedirect("blog_list.html");
    }
}
