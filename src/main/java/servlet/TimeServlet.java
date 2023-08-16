package servlet;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private final TemplateEngine templateEngine;

    public TimeServlet() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");

        String timeZone = request.getParameter("timezone");

        String timeZoneCookieValue = timeZone != null ? timeZone.replace(" ", "+") : null;

        if (timeZoneCookieValue != null) {
            Cookie lastTimezoneCookie = new Cookie("lastTimezone", timeZoneCookieValue);
            lastTimezoneCookie.setMaxAge(3600 * 24 * 30);
            response.addCookie(lastTimezoneCookie);
        }

        Cookie[] cookies = request.getCookies();
        if (timeZone == null && cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    timeZone = cookie.getValue().replace("+", " ");
                    break;
                }
            }
        }

        ZoneId zoneId;

        if (timeZone != null && !timeZone.isEmpty() && !timeZone.equals("UTC")) {
            zoneId = ZoneId.of(timeZone.replace(" ", "+"));
            timeZone = (timeZone.replace(" ", "+"));
        } else {
            zoneId = ZoneId.of("UTC");
            timeZone = "UTC";
        }

        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + zoneId;

        Context context = new Context();
        context.setVariable("timezone", timeZone);
        context.setVariable("formattedTime", formattedTime);

        templateEngine.process("timeTemplate", context, response.getWriter());
    }

}

