package com.example.bankingprojectfinal.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class demoController {
    @GetMapping
    public String getName(){
        return "hello";
    }
}
