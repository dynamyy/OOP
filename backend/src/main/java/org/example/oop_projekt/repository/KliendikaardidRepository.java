package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.Kasutaja;
import org.example.oop_projekt.mudel.Kliendikaardid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KliendikaardidRepository extends JpaRepository<Kliendikaardid, Long> {
    List<Kliendikaardid> findByKasutajaId(Kasutaja kasutaja);
}
