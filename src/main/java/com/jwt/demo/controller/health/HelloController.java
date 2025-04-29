package com.jwt.demo.controller.health;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hello")
public class HelloController {

    @GetMapping("/world")
    public String helloWorld() {
        return "Hello World!!";
    }
}
