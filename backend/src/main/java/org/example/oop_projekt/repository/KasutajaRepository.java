package org.example.oop_projekt.repository.andmepääsukiht;

import org.example.oop_projekt.mudel.Kasutaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KasutajaRepository extends JpaRepository<Kasutaja, Long> {
    Kasutaja findByEmail(String email);
}
