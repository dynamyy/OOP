package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private List<Ostukorv> ostukorvid;

    public Kasutaja(String email, String parool, List<Ostukorv> ostukorvid) {
        this.email = email;
        this.parool = parool;
        this.ostukorvid = ostukorvid;
    }

    public Kasutaja() {
    }
}
