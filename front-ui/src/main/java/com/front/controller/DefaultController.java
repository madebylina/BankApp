package com.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    /**
     * GET "/"
     * @return - редирект на "/main"
     */
    @GetMapping("")
    public String homePage() {
        return "redirect:/main";
    }
}
