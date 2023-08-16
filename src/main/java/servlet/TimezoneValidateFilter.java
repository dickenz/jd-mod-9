package servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(value = "/time", servletNames = "TimeServlet")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String timezone = request.getParameter("timezone");

        if (timezone != null && !isValidTimeZone(timezone)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write("Invalid timezone");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isValidTimeZone(String timezone) {

        timezone = timezone.replace("UTC", "").replace(" ", "+");
        try {
            int offset = Integer.parseInt(timezone);
            return offset >= -12 && offset <= 14;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

