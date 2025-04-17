package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ToodeOstukorvis")
@Table(name = "tooted_ostukorvis")
@Getter
@Setter
public class ToodeOstukorvis {

    @Id
    @SequenceGenerator(
            name = "toode_ostukorvis_sequence",
            sequenceName = "toode_ostukorvis_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "toode_ostukorvis_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "ostukorv_id")
    private Ostukorv ostukorv;

    @OneToOne
    @JoinColumn(name = "toode_id")
    private Toode toode;

    @Column(
            name = "kogus"
    )
    private Integer kogus;

    public ToodeOstukorvis(Ostukorv ostukorv, Toode toode, Integer kogus) {
        this.ostukorv = ostukorv;
        this.toode = toode;
        this.kogus = kogus;
    }

    public ToodeOstukorvis() {
    }
}
