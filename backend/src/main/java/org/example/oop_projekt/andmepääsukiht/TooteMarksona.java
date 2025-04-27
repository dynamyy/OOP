package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "ToodeMarksona")
@Table(name = "toote_marksonad")
@Data
public class TooteMarksona {

    @Id
    @SequenceGenerator(
            name = "toode_marksona_sequence",
            sequenceName = "toode_marksona_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "toode_marksona_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(name = "marksona")
    private String marksona;

    @ManyToOne
    @JoinColumn(name = "toode_ostukorvis_id")
    private ToodeOstukorvis toodeOstukorvis;

    public TooteMarksona(String marksona, ToodeOstukorvis toodeOstukorvis) {
        this.marksona = marksona;
        this.toodeOstukorvis = toodeOstukorvis;
    }

    public TooteMarksona() {
    }
}
