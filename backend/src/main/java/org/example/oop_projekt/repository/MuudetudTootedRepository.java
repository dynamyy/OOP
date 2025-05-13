package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.MuudetudToode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MuudetudTootedRepository extends JpaRepository<MuudetudToode, Long> {



}
