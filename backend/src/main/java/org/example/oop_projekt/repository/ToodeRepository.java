package org.example.oop_projekt.repository;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.mudel.Pood;
import org.example.oop_projekt.mudel.Toode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("""
            SELECT t.id FROM Toode t
            WHERE t.pood = :pood
            AND (t.viimatiUuendatud < :vanemKui OR t.viimatiUuendatud IS NULL)
            AND NOT EXISTS (
                SELECT 1 FROM EbasobivToode et WHERE et.toode = t
            )
            AND NOT EXISTS (
                SELECT 1 FROM ToodeOstukorvis tok WHERE tok.coopToode = t
                                                    OR tok.prismaToode = t
                                                    OR tok.barboraToode = t
                                                    OR tok.rimiToode = t
                                                    OR tok.selverToode = t
            )
            """)
    List<Long> leiaTootedKustutamiseks(@Param("pood") Pood pood, @Param("vanemKui") LocalDateTime vanemKui);

    @Modifying
    @Transactional
    @Query("DELETE FROM Toode t WHERE t.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);

    /*
    @Modifying
    @Transactional
    @Query("")
    void uuendaTooteHinda(@Param("uushind") double hind, @Param("id") int id);

     */
}