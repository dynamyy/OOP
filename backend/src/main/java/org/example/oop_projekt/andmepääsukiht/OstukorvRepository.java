package org.example.oop_projekt.andmepääsukiht;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface OstukorvRepository extends JpaRepository<Ostukorv, Long> {


    //Ostukorv LooOstukorv(Map<String, String> märksõnad); - siia ei saa mapi panna, tuleb mingi muu lahendus välja mõelda
}
