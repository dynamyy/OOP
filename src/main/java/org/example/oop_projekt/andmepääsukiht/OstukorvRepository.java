package org.example.oop_projekt.andmepääsukiht;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OstukorvRepository extends JpaRepository<Ostukorv, Long> {
}
