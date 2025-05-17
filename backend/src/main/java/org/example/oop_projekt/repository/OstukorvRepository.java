package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.Kasutaja;
import org.example.oop_projekt.mudel.Ostukorv;
import org.example.oop_projekt.mudel.Toode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OstukorvRepository extends JpaRepository<Ostukorv, Long> {

    Ostukorv findOstukorvById(long id);

    List<Ostukorv> findOstukorvByKasutaja(Kasutaja kasutaja);
}
