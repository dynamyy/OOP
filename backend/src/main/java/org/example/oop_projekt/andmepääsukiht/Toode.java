package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity(name = "Toode")
@Table(name = "tooted")
@Getter
@Setter
public class Toode {

    @Id
    @SequenceGenerator(
            name = "toode_sequence",
            sequenceName = "toode_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "toode_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "nimetus",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String nimetus;

    @Column(
            name = "tyki_hind"
    )
    private double tukiHind;

    @Column(
            name = "hulga_hind"
    )
    private double hulgaHind;

    @Column(
            name = "yhik",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String yhik;

    @Column(
            name = "hind_kliendi"
    )
    private double hindKliendi;

    @Column(
            name = "hulga_hind_kliendi"
    )
    private double hulgaHindKliendi;

    @ManyToOne
    @JoinColumn(name = "pood_id")
    private Pood pood;

    public Toode(String nimetus,
                 String yhik,
                 double hindKliendi,
                 double hulgaHindKliendi,
                 Pood pood,
                 double hulgaHind,
                 double tukiHind) {
        this.nimetus = nimetus;
        this.yhik = yhik;
        this.hindKliendi = hindKliendi;
        this.hulgaHindKliendi = hulgaHindKliendi;
        this.pood = pood;
        this.hulgaHind = hulgaHind;
        this.tukiHind = tukiHind;
    }

    public Toode() {
    }

}

