package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "toode_ostukorvis_id")
    private ToodeOstukorvis toodeOstukorvis;

    @OneToOne
    @JoinColumn(
            name = "toode_id"
    )
    private Toode toode;

    public EbasobivToode(ToodeOstukorvis toodeOstukorvis, Toode toode) {
        this.toodeOstukorvis = toodeOstukorvis;
        this.toode = toode;
    }

    public EbasobivToode() {
    }
}
