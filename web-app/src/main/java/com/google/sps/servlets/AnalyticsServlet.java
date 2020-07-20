import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@WebServlet("/analytics")
public class AnalyticsServlet extends HttpServlet {
  private Node root;

  private class Node {
    Double x, y, length;
    Node NW, NE, SE, SW;
    int numReports;
    ArrayList[] reports;
  }

}