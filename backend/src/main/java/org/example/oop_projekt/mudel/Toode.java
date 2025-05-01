package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(
            name = "toote_pilt_url",
            columnDefinition = "TEXT"
    )
    private String tootePiltURL;

    @ManyToOne
    @JoinColumn(name = "pood_id")
    private Pood pood;

    public Toode(String nimetus,
                 String yhik,
                 double hindKliendi,
                 double hulgaHindKliendi,
                 Pood pood,
                 double hulgaHind,
                 double tukiHind,
                 String tootePiltURL) {
        this.nimetus = nimetus;
        this.yhik = yhik;
        this.hindKliendi = hindKliendi;
        this.hulgaHindKliendi = hulgaHindKliendi;
        this.pood = pood;
        this.hulgaHind = hulgaHind;
        this.tukiHind = tukiHind;
        this.tootePiltURL = tootePiltURL;
    }

    public Toode() {
    }
}

