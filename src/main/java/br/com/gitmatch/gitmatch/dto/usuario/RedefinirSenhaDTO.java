package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.Data;

@Data
public class RedefinirSenhaDTO {
    private String email;
    private String novaSenha;
}
