package org.example.oop_projekt.Conf;


import org.example.oop_projekt.andmepääsukiht.ToodeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


import java.util.List;

@Configuration
public class ToodeConf {

    @Bean(name = "commandLineRunner")
    @Order(1)


    CommandLineRunner commandLineRunner(ToodeRepository repo){
        return args -> {

            //repo.saveAll();
        };
    }

}
