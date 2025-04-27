package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "kasutaja_id")
    private Kasutaja kasutaja;

    @OneToMany(mappedBy = "ostukorv")
    private List<ToodeOstukorvis> tootedOstukorvis;

    public Ostukorv(List<ToodeOstukorvis> tootedOstukorvis) {
        this.tootedOstukorvis = tootedOstukorvis;
    }

    public Ostukorv() {
    }
}
