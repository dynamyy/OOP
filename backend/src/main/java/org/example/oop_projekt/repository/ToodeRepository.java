package org.example.oop_projekt.repository;

import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.mudel.Pood;
import org.example.oop_projekt.mudel.Toode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToodeRepository extends JpaRepository<Toode, Long>, JpaSpecificationExecutor<Toode> {

    Toode findToodeByNimetusAndPood(String nimetus, Pood pood);

    Toode findToodeByTooteKood(String tooteKood);

    Toode findToodeById(Long id);

    @Query("SELECT new org.example.oop_projekt.DTO.ToodeDTO(t.id, t.nimetus, t.hindKliendi, t.hulgaHindKliendi, t.yhik, CASE WHEN t.hulgaHindKliendi < t.hindKliendi THEN 'true' ELSE 'false' END, t.pood.nimi, t.tootePiltURL) " +
            "FROM Toode t " +
            "WHERE LOWER(t.nimetus) LIKE LOWER(:nimetus)" +
            "ORDER BY 1")

    List<ToodeDTO> leiaToodeNimega(@Param("nimetus") String nimetus);


    /*
    @Modifying
    @Transactional
    @Query("")
    void uuendaTooteHinda(@Param("uushind") double hind, @Param("id") int id);

     */
}