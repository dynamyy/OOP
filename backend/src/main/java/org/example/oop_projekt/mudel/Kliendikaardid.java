package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "kliendikaart")
@Table(name = "kliendikaardid")
@Data
public class Kliendikaardid {
    @Id
    @SequenceGenerator(
            name = "kliendikaart_sequence",
            sequenceName = "kliendikaart_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "kliendikaart_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @ManyToOne
    @JoinColumn(
            name = "kasutaja",
            nullable = false
    )
    private Kasutaja kasutaja;

    @Column(
            name = "poe_nimi",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String poeNimi;

    public Kliendikaardid(Kasutaja kasutaja, String poeNimi) {
        this.kasutaja = kasutaja;
        this.poeNimi = poeNimi;
    }

    public Kliendikaardid() {
    }
}
