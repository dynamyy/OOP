package org.example.oop_projekt.repository.andmepääsukiht;

import org.example.oop_projekt.mudel.Pood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoodRepository extends JpaRepository<Pood, Long> {

    Pood findPoodByNimi(String nimi);
}
