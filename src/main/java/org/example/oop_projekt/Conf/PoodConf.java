package org.example.oop_projekt.Conf;

import org.example.oop_projekt.andmepääsukiht.Pood;
import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.List;

@Configuration
public class PoodConf {

    @Bean(name = "poodCommandLineRunner")
    @Order(1)
    CommandLineRunner commandLineRunner(PoodRepository repo){
        return args -> {

            Pood coop = new Pood("Coop", new HashSet<>());
            Pood selver = new Pood("Selver", new HashSet<>());
            Pood barbora = new Pood("Barbora", new HashSet<>());
            Pood rimi = new Pood("Rimi", new HashSet<>());
            Pood prisma = new Pood("Prisma", new HashSet<>());

            repo.saveAll(List.of(coop, selver, barbora, rimi, prisma));

        };
    }

}
