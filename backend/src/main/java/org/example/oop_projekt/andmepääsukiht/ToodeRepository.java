package org.example.oop_projekt.andmepääsukiht;

import org.example.oop_projekt.DTO.ToodeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToodeRepository extends JpaRepository<Toode, Long> {

    Toode findToodeByNimetusAndPood(String nimetus, Pood pood);

    @Query("SELECT new org.example.oop_projekt.DTO.ToodeDTO(t.nimetus, t.hindKliendi, t.hulgaHindKliendi, t.yhik, CASE WHEN t.hulgaHindKliendi < t.hindKliendi THEN 'true' ELSE 'false' END, t.pood.nimi) " +
            "FROM Toode t " +
            "WHERE LOWER(t.nimetus) LIKE LOWER(:nimetus)" +
            "ORDER BY 1")
    List<ToodeDTO> leiaToodeNimega(@Param("nimetus") String nimetus);


}
