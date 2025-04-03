package org.example.oop_projekt.andmepääsukiht;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToodeRepository extends JpaRepository<Toode, Long> {

    Toode findToodeById(long id);

}
