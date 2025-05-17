package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity(name = "Ostukorv")
@Table(name = "ostukorvid")
@Data
public class Ostukorv {

    @Id
    @SequenceGenerator(
            name = "ostukorv_sequence",
            sequenceName = "ostukorv_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ostukorv_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "nimi",
            nullable = false
    )
    private String nimi;

    @ManyToOne
    @JoinColumn(name = "kasutaja_id")
    @ToString.Exclude
    private Kasutaja kasutaja;

    @OneToMany(mappedBy = "ostukorv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToodeOstukorvis> tootedOstukorvis;

    public Ostukorv(String nimi, List<ToodeOstukorvis> tootedOstukorvis) {
        this.nimi = nimi;
        this.tootedOstukorvis = tootedOstukorvis;
    }

    public Ostukorv() {
    }
}
