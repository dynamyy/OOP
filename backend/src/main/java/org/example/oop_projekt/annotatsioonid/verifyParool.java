package org.example.oop_projekt.annotatsioonid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kontrollib parooli kehtivust enne meetodi käivitamist.
 * Meetod, millele annotatsioon lisatakse, peab võtma argumendiks dto
 * objekti, mis implementeerib ParoolDTO liidest. Lisaks peab meetod
 * võtma argumendiks kas TokenDTO või EmailDTO liidest implementeeriva
 * objekti. Sama argument võib implementeerida ka mõlemat liidest.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface verifyParool {
}
