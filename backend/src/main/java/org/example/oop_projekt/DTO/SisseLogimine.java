package org.example.oop_projekt.DTO;


public record SisseLogimine (
        String email,
        String parool
) {
    /**
     * Igaks-juhuks toString override, et ei väljastataks paroole
     * @return kasutaja email
     */
    @Override
    public String toString() { return email; }
}
