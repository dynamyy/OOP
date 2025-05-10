package org.example.oop_projekt.annotatsioonid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kontrollib authTokeni kehtivust enne meetodi käivitamist.
 * Meetod, millele annotatsioon lisatakse, peab võtma argumendiks dto
 * objekti, mis implementeerib TokenDTO liidest. Vastasel juhul tagastatakse
 * alati veateade.
 * Kui authToken ei kehti, siis visataske TokenKehtetuException
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface verifyToken {
}
