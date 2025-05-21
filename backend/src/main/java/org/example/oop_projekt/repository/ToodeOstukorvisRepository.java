package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.Ostukorv;
import org.example.oop_projekt.mudel.ToodeOstukorvis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToodeOstukorvisRepository extends JpaRepository<ToodeOstukorvis, Long> {
    List<ToodeOstukorvis> findToodeOstukorvisByOstukorv(Ostukorv ostukorv);

    ToodeOstukorvis findToodeOstukorvisById(long id);
}
