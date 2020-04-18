import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Marek on 13/04/2017.
 */
@WebServlet(name = "CounterServlet")
public class CounterServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Integer counter = 0;
        getServletContext().setAttribute("counter", counter);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer counter = (Integer) getServletContext().getAttribute("counter");
        counter++;

        getServletContext().setAttribute("counter", counter);

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {

            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>CounterServlet using doGet() to increment local variable</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>You are our " + counter + ". customer</h1>");

            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
