package org.example.oop_projekt.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.example.oop_projekt.mudel.Toode;

import jakarta.persistence.criteria.Predicate;
import java.util.List;

public class ToodeSpecification {

    public static Specification<Toode> nimetusSisaldabKoiki(List<String> marksonad) {
        return (root, query, cb) -> {
            Predicate[] koikSisaldavad = marksonad.stream()
                    .map(m -> cb.like(cb.lower(root.get("nimetus")), "%" + m.toLowerCase() + "%"))
                    .toArray(Predicate[]::new);
            return cb.and(koikSisaldavad);
        };
    }

    public static Specification<Toode> nimetusEiSisaldaUhtegi(List<String> marksonad) {
        return (root, query, cb) -> {
            Predicate[] valistused = marksonad.stream()
                    .map(m -> cb.notLike(cb.lower(root.get("nimetus")), "%" + m.toLowerCase() + "%"))
                    .toArray(Predicate[]::new);
            return cb.and(valistused);
        };
    }
}
