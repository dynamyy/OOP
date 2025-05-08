package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "EbasobivToode")
@Table(name = "ebasobivad_tooted")
@Getter
@Setter
public class EbasobivToode {

    @Id
    @SequenceGenerator(
            name = "ebasobiv_toode_sequence",
            sequenceName = "ebasobiv_toode_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ebasobiv_toode_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "ebasobiv_toode_id"),
            inverseJoinColumns = @JoinColumn(name = "toode_ostukorvis_id"),
            name = "toode_ostukorvis_ebasobiv_toode_tabel"
    )
    private List<ToodeOstukorvis> tootedOstukorvis;

    @OneToOne
    @JoinColumn(
            name = "toode_id"
    )
    private Toode toode;

    public EbasobivToode(List<ToodeOstukorvis> tootedOstukorvis, Toode toode) {
        this.tootedOstukorvis = tootedOstukorvis;
        this.toode = toode;
    }

    public EbasobivToode() {
    }
}
