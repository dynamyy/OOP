package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.TooteMarksona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TooteMarksonaRepository extends JpaRepository<TooteMarksona, Long> {
}
