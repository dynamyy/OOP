package org.example.oop_projekt.repository;

import org.example.oop_projekt.mudel.MuudetudToode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MuudetudTootedRepository extends JpaRepository<MuudetudToode, Long> {

    @Query("SELECT m FROM Muudetudtoode m WHERE m.kasutaja.id = :kasutajaId AND m.muutmisAeg > :praegu")
    List<MuudetudToode> leiaKehtivadMuudetudTooted(@Param("kasutajaId") Long kasutajaId, @Param("praegu") LocalDateTime praegu);

    @Query("SELECT m FROM Muudetudtoode m WHERE m.kasutaja.id = :kasutajaId AND CAST(m.muudetudTooteID AS string) = :tooteId AND m.muutmisAeg > :praegu")
    MuudetudToode leiaKehtivMuudetudToodeKonkreetne(@Param("kasutajaId") Long kasutajaId, @Param("tooteId") String tooteId, @Param("praegu") LocalDateTime praegu);


}
