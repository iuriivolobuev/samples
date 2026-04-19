package sample.webapp.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class HtmlController {
    @GetMapping("/")
    void indexPage(HttpServletResponse response) {
        response.setHeader("Location", "dogs");
        response.setStatus(302);
    }

    @GetMapping("/dogs")
    public String dogsPage() {
        return "/static/html/dogs.html";
    }
}
