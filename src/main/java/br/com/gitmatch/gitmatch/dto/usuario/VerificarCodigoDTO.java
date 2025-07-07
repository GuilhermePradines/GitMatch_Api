package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.Data;

@Data
public class VerificarCodigoDTO {
    private String email;
    private String token;
}
