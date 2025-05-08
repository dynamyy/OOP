package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.EbasobivToode;
import org.example.oop_projekt.mudel.Toode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EbasobivToodeRepository extends JpaRepository<EbasobivToode, Long> {
    EbasobivToode findByToode(Toode toode);
}
