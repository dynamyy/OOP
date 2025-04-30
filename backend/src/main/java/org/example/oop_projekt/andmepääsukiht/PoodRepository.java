package org.example.oop_projekt.andmepääsukiht;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PoodRepository extends JpaRepository<Pood, Long> {

    Pood findPoodByNimi(String nimi);
}
