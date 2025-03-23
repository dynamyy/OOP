package org.example.oop_projekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class OopProjektApplication {

    @RequestMapping
    public String index() {
        return "index.html";
    }

    public static void main(String[] args) {

        SpringApplication.run(OopProjektApplication.class, args);
    }

}