package sit.int221.itbkkbackend.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriExtractor {
    public String getBoardId(HttpServletRequest request){
        Pattern pattern = Pattern.compile(".*/boards/([^/]+)(?:/[^/]+.*)?");
        Matcher matcher = pattern.matcher(request.getRequestURI());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
}
