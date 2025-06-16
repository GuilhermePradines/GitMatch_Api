package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String senha;
}
