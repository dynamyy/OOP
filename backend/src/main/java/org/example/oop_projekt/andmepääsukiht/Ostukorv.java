package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "Ostukorv")
@Table(name = "ostukorvid")
@Getter
@Setter
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

    @OneToMany(mappedBy = "ostukorv")
    private List<ToodeOstukorvis> tootedOstukorvis;

    public Ostukorv(List<ToodeOstukorvis> tootedOstukorvis) {
        this.tootedOstukorvis = tootedOstukorvis;
    }

    public Ostukorv() {
    }
}
