package org.example.oop_projekt.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.example.oop_projekt.mudel.Toode;

import jakarta.persistence.criteria.Predicate;
import java.util.List;

public class ToodeSpecification {

    public static Specification<Toode> nimetusSisaldabKõiki(List<String> märksõnad) {
        return (root, query, cb) -> {
            Predicate[] kõikSisaldavad = märksõnad.stream()
                    .map(m -> cb.like(cb.lower(root.get("nimetus")), "%" + m.toLowerCase() + "%"))
                    .toArray(Predicate[]::new);
            return cb.and(kõikSisaldavad);
        };
    }

    public static Specification<Toode> nimetusEiSisaldaÜhtegi(List<String> märksõnad) {
        return (root, query, cb) -> {
            Predicate[] välistused = märksõnad.stream()
                    .map(m -> cb.notLike(cb.lower(root.get("nimetus")), "%" + m.toLowerCase() + "%"))
                    .toArray(Predicate[]::new);
            return cb.and(välistused);
        };
    }
}
