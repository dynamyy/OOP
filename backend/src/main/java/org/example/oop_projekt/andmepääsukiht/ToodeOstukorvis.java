package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "ToodeOstukorvis")
@Table(name = "tooted_ostukorvis")
@Data
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
    @JoinColumn(
            name = "toode_id",
            nullable = false
    )
    private Toode toode;

    @OneToMany(mappedBy = "toodeOstukorvis")
    private List<TooteMarksona> tooteMarksonad;

    @Column(
            name = "kogus"
    )
    private Integer kogus;

    public ToodeOstukorvis(Ostukorv ostukorv,
                           List<TooteMarksona> tooteMarksonad,
                           Integer kogus) {
        this.ostukorv = ostukorv;
        this.tooteMarksonad = tooteMarksonad;
        this.kogus = kogus;
    }

    public ToodeOstukorvis() {
    }
}
