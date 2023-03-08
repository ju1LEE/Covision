package egovframework.covision.groupware.aws.s3;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AwsS3Router extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestURI = req.getRequestURI();
        String[] arrURL = requestURI.split("/");
        StringBuilder key = new StringBuilder();
        int appendCnt = 0;
        if(arrURL.length>3) {
            for (int i = 0; i < arrURL.length; i++) {
                if (i > 2) {
                    if(appendCnt>0) {
                        key.append("/");
                    }
                    key.append(arrURL[i]);
                    appendCnt++;
                }
            }
            resp.sendRedirect("/groupware/aws/s3redirect.do?key="+key);
        }else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND");
        }
    }
}