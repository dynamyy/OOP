package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "Muudetudtoode")
@Table(name = "muudetud_tooted")
@Getter
@Setter
public class MuudetudToode {
    @Id
    @SequenceGenerator(
            name = "muudetudtoode_sequence",
            sequenceName = "muudetudtoode_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "muudetudtoode_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "yhikuhind",
            nullable = false
    )
    private double yhikuhind;

    @Column(
            name = "tykihind",
            nullable = false
    )
    private double tykihind;

    @Column(
            name = "muutmisaeg",
            nullable = false
    )
    private LocalDateTime muutmisAeg;

    @Column(
            name = "muudetudTooteID",
            nullable = false
    )
    private Long muudetudTooteID;

    @ManyToOne
    @JoinColumn(name = "kasutaja_id", nullable = false)
    private Kasutaja kasutaja;

    public MuudetudToode (
            Kasutaja kasutaja,
            double yhikuhind,
            double tykihind,
            LocalDateTime muutmisAeg,
            Long muudetudTooteID
    ){
        this.kasutaja = kasutaja;
        this.yhikuhind = yhikuhind;
        this.tykihind = tykihind;
        this.muutmisAeg = muutmisAeg; //Halb muutujanimi, siia salvestatakse hinnamuutuse kehtivusaeg
        this.muudetudTooteID = muudetudTooteID;
    }

    public MuudetudToode() {
    }

    // Vaja on id-d(serial), kasutaja id-d, uus kilohind ja tükihind, kuupäev ning muudetud toote id-d

    // Kui kasutajale kuvatakse kõik tooted, tuleks iga toote peal vaadata,
    // ega selle hinda pole konkreetsel kasutajal muudetud ning ega tehtud muudatus pole aegunud
    // Kui on, siis tuleks kuvada see toode uue hinnaga


}
