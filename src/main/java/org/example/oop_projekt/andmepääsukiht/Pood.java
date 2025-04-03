package org.example.oop_projekt.andmepääsukiht;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity(name = "Pood")
@Table(name = "poed")
@Getter
@Setter
public class Pood {

    @Id
    @SequenceGenerator(
            name = "pood_sequence",
            sequenceName = "pood_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pood_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(
            name = "nimi",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String nimi;

    @ManyToMany
    @JoinTable(
            name = "pood_toode",
            joinColumns = @JoinColumn(name = "pood_id"),
            inverseJoinColumns = @JoinColumn(name = "toode_id")
    )
    private Set<Toode> tooted;

    public Pood(String nimi, Set<Toode> tooted) {
        this.nimi = nimi;
        this.tooted = tooted;
    }

    public Pood() {
    }

    public void lisaToode(Toode toode) {
        this.tooted.add(toode);
    }

    @Override
    public String toString() {
        return "Pood{" +
                "id=" + id +
                ", nimi='" + nimi + '\'' +
                ", tooted=" + tooted +
                '}';
    }
}
