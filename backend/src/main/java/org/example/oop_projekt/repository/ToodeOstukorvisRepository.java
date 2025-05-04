package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.ToodeOstukorvis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToodeOstukorvisRepository extends JpaRepository<ToodeOstukorvis, Long> {

}
