package org.example.oop_projekt.andmepääsukiht;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToodeRepository extends JpaRepository<Toode, Long> {

    Toode findToodeByNimetusAndPood(String nimetus, Pood pood);

}
