package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Toode")
@Table(name = "tooted")
@Getter
@Setter
public class Toode {

    @Id
    @SequenceGenerator(
            name = "toode_sequence",
            sequenceName = "toode_sequence",
            allocationSize = 1
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "nimetus",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String nimetus;

    @Column(
            name = "tyki_hind",
            nullable = false
    )
    private double tukiHind;

    @Column(
            name = "pood",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String pood;

}

