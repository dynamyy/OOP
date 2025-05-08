package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    @JoinColumn(
            name = "coop_toode_id"
    )
    private Toode coopToode;

    @OneToOne
    @JoinColumn(
            name = "prisma_toode_id"
    )
    private Toode prismaToode;

    @OneToOne
    @JoinColumn(
            name = "barbora_toode_id"
    )
    private Toode barboraToode;

    @OneToOne
    @JoinColumn(
            name = "rimi_toode_id"
    )
    private Toode rimiToode;

    @OneToOne
    @JoinColumn(
            name = "selver_toode_id"
    )
    private Toode selverToode;

    @OneToMany(mappedBy = "toodeOstukorvis", cascade = CascadeType.ALL)
    private List<TooteMarksona> tooteMarksonad;

    @Column(
            name = "kogus"
    )
    private Integer kogus;

    @ManyToMany(mappedBy = "tootedOstukorvis", cascade = CascadeType.ALL)
    private List<EbasobivToode> ebasobivadTooted;

    public ToodeOstukorvis(
            Ostukorv ostukorv,
            Toode coopToode,
            Toode prismaToode,
            Toode barboraToode,
            Toode rimiToode,
            List<TooteMarksona> tooteMarksonad,
            Integer kogus,
            List<EbasobivToode> ebasobivadTooted,
            Toode selverToode) {
        this.ostukorv = ostukorv;
        this.coopToode = coopToode;
        this.prismaToode = prismaToode;
        this.barboraToode = barboraToode;
        this.rimiToode = rimiToode;
        this.tooteMarksonad = tooteMarksonad;
        this.kogus = kogus;
        this.ebasobivadTooted = ebasobivadTooted;
        this.selverToode = selverToode;
    }

    public ToodeOstukorvis(Ostukorv ostukorv, List<TooteMarksona> tooteMarksonad, Integer kogus, List<EbasobivToode> ebasobivadTooted) {
        this.ostukorv = ostukorv;
        this.tooteMarksonad = tooteMarksonad;
        this.kogus = kogus;
        this.ebasobivadTooted = ebasobivadTooted;
    }

    public ToodeOstukorvis() {
    }
}
