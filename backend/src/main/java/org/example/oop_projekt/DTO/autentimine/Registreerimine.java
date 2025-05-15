package org.example.oop_projekt.DTO.autentimine;

import java.util.List;

public record Registreerimine(
        String email,
        String parool,
        List<String> kliendikaardid
) {
    /**
     * Igaks-juhuks toString override, et ei väljastataks paroole
     * @return kasutaja email
     */
    @Override
    public String toString() {
        return email;
    }
}
