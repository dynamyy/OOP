package org.example.oop_projekt.mudel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "toode_sequence"
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
            columnDefinition = "NUMERIC(10, 2)"
    )
    private double tukiHind;

    @Column(
            name = "hulga_hind",
            columnDefinition = "NUMERIC(10, 2)"
    )
    private double hulgaHind;

    @Column(
            name = "yhik",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String yhik;

    @Column(
            name = "hind_kliendi",
            columnDefinition = "NUMERIC(10, 2)"
    )
    private double hindKliendi;

    @Column(
            name = "hulga_hind_kliendi",
            columnDefinition = "NUMERIC(10, 2)"
    )
    private double hulgaHindKliendi;

    @Column(
            name = "toote_pilt_url",
            columnDefinition = "TEXT"
    )
    private String tootePiltURL;

    @Column(
            name = "toote_kood"
    )
    private String tooteKood;

    @Column(
            name = "viimati_uuendatud"
    )
    private LocalDateTime viimatiUuendatud;

    @ManyToOne
    @JoinColumn(name = "pood_id")
    private Pood pood;

    @OneToMany(mappedBy = "barboraToode")
    private List<ToodeOstukorvis> barboraOdavaimadTooted;

    @OneToMany(mappedBy = "coopToode")
    private List<ToodeOstukorvis> cooopOdavaimadTooted;

    @OneToMany(mappedBy = "rimiToode")
    private List<ToodeOstukorvis> rimiOdavaimadTooted;

    @OneToMany(mappedBy = "prismaToode")
    private List<ToodeOstukorvis> prismaOdavaimadTooted;

    @OneToMany(mappedBy = "selverToode")
    private List<ToodeOstukorvis> selverOdavaimadTooted;

    public Toode(String nimetus,
                 String yhik,
                 double hindKliendi,
                 double hulgaHindKliendi,
                 Pood pood,
                 double hulgaHind,
                 double tukiHind,
                 String tootePiltURL,
                 String tooteKood,
                 LocalDateTime viimatiUuendatud) {
        this.nimetus = nimetus;
        this.tukiHind = tukiHind;
        this.hulgaHind = hulgaHind;
        this.yhik = yhik;
        this.hindKliendi = hindKliendi;
        this.hulgaHindKliendi = hulgaHindKliendi;
        this.tootePiltURL = tootePiltURL;
        this.tooteKood = tooteKood;
        this.viimatiUuendatud = viimatiUuendatud;
        this.pood = pood;
        this.barboraOdavaimadTooted = new ArrayList<>();
        this.cooopOdavaimadTooted = new ArrayList<>();
        this.rimiOdavaimadTooted = new ArrayList<>();
        this.prismaOdavaimadTooted = new ArrayList<>();
        this.selverOdavaimadTooted = new ArrayList<>();
    }

    public Toode() {
    }

    @Override
    public String toString() {
        return "Toode{" +
                "id=" + id +
                ", nimetus='" + nimetus + '\'' +
                ", tukiHind=" + tukiHind +
                ", hulgaHind=" + hulgaHind +
                ", yhik='" + yhik + '\'' +
                ", hindKliendi=" + hindKliendi +
                ", hulgaHindKliendi=" + hulgaHindKliendi +
                ", tootePiltURL='" + tootePiltURL + '\'' +
                ", tooteKood='" + tooteKood + '\'' +
                ", viimatiUuendatud=" + viimatiUuendatud +
                ", pood=" + pood +
                ", barboraOdavaimadTooted=" + barboraOdavaimadTooted +
                ", cooopOdavaimadTooted=" + cooopOdavaimadTooted +
                ", rimiOdavaimadTooted=" + rimiOdavaimadTooted +
                ", prismaOdavaimadTooted=" + prismaOdavaimadTooted +
                ", selverOdavaimadTooted=" + selverOdavaimadTooted +
                '}';
    }
}

