import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-07-11
 * Time: 15:41
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        设置请求的编码
        req.setCharacterEncoding("utf8");
//        设置响应的编码,告诉servlet按照什么样的格式来构造响应
        resp.setContentType("text/html;charset=utf8");
//        读取参数中的用户名和密码
//        如果密码包含中文,读取可能会乱码
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if(username == "" || "".equals(username) || password == "" || "".equals(password)){
            String html = "<h3> 登陆失败!缺少 用户名 或者 密码 </h3>";
            resp.getWriter().write(html);
            return;
        }
//        读取数据库,看用户名是否存在,密码是否匹配
        UserDao userDao = new UserDao();
        User user = userDao.selectByUsername(username);
        if( user == null || !password.equals(user.getPassword()) ) {
//            用户名不存在,或者密码错误
            String html = "<h3>登录失败! 用户名或者密码错误</h3>";
            resp.getWriter().write(html);
            return;
        }
//        登录成功,就创建会话,保存用户信息
        HttpSession session = req.getSession(true);
        session.setAttribute("user",user);
        resp.sendRedirect("blog_list.html");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf8");
//        判定登录状态:是否有http session对象,看session对象里有没有user
//        如果用户未登录,会话就拿不到
//        为什么可以取到session,因为之前写登录页时,登录成功就会创建会话,有存就有取~
        HttpSession session = req.getSession(false);
        if(session == null){
//            未登录就返回一个空的user对象
            User user = new User();
            String resqJson = objectMapper.writeValueAsString(user);
            resp.getWriter().write(resqJson);
            return;
        }
//        取user对象
        User user = (User) session.getAttribute("user");
        if (user == null) {
//            因为后续的注销操作就是删除session里的user对象,
//            因此有session,没有user也视为未登录状态
//            而且页面上也要显示出当前用户信息,而用户名是从user里来的,没有user怎么行捏
            user = new User();
            String resqJson = objectMapper.writeValueAsString(user);
            resp.getWriter().write(resqJson);
            return;
        }
//        确实成功取出了user对象,就直接返回即可
        String resqJson = objectMapper.writeValueAsString(user);
        resp.getWriter().write(resqJson);
    }
}
