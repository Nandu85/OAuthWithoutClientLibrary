package com.example.pureoauth2gradle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/loginPage")
    public String getlogin(){
        return "login";
    }

}
