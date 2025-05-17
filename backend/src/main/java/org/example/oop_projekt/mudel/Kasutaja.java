package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Kasutaja")
@Table(name = "kasutajad")
@Data
public class Kasutaja {
    @Id
    @SequenceGenerator(
            name = "kasutaja_sequence",
            sequenceName = "kasutaja_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "kasutaja_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "email",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String email;

    @Column(
            name = "parool",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String parool;

    @OneToMany(mappedBy = "kasutaja")
    @ToString.Exclude
    private List<Ostukorv> ostukorvid;

    @OneToMany(mappedBy = "kasutaja")
    private List<Kliendikaardid> kliendikaardid;

    @OneToMany(mappedBy = "kasutaja") // KEVIN LISAS
    List<MuudetudToode> muudetudTooted; // KEVIN LISAS

    public Kasutaja(String email, String parool, List<Ostukorv> ostukorvid, List<MuudetudToode> muudetudTooted ) { // KEVIN LISAS muudetudTooted
        this.email = email;
        this.parool = parool;
        this.ostukorvid = ostukorvid;
        this.kliendikaardid = new ArrayList<>();
        this.muudetudTooted = muudetudTooted; // KEVIN LISAS
    }

    public Kasutaja() {
    }
}
