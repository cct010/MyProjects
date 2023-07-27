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
 * Date: 2023-07-22
 * Time: 18:05
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession httpSession = req.getSession(false);
        if(httpSession == null){
//            未登录就提示未登录
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前未登录!");
        }
        httpSession.removeAttribute("user");
        resp.sendRedirect("login.html");
    }
}
