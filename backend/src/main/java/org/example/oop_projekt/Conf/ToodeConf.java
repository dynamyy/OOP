package org.example.oop_projekt.Conf;


import org.example.oop_projekt.repository.ToodeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


@Configuration
public class ToodeConf {

    @Bean(name = "toodeCommandLineRunner")
    @Order(2)


    CommandLineRunner commandLineRunner(ToodeRepository repo){
        return args -> {
            //Igalt lehelt saadud tooted salvestame siin funktsioonis andmebaasi
            //repo.saveAll();
        };
    }

}
