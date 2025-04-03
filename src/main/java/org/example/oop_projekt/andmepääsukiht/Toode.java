package org.example.oop_projekt.andmepääsukiht;

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
            name = "hulga_hind",
            nullable = false
    )
    private double hulgaHind;

    @Column(
            name = "yhik",
            nullable = false
    )
    private String yhik;

    @Column(
            name = "pood",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String pood;

    public Toode(long id, String nimetus, double tukiHind, double hulgaHind, String yhik, String pood) {
        this.id = id;
        this.nimetus = nimetus;
        this.tukiHind = tukiHind;
        this.hulgaHind = hulgaHind;
        this.yhik = yhik;
        this.pood = pood;
    }

    public Toode() {
    }
}

