package org.example.oop_projekt.DTO.autentimine;


public record SisseLogimine (
        String email,
        String parool
) implements EmailDTO, ParoolDTO {
    /**
     * Igaks-juhuks toString override, et ei väljastataks paroole
     * @return kasutaja email
     */
    @Override
    public String toString() { return email; }
}