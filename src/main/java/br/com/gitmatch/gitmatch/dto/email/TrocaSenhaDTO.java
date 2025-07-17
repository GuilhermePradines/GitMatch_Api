package br.com.gitmatch.gitmatch.dto.email;

import lombok.Data;

@Data
public class TrocaSenhaDTO {
    private String email;
    private String codigo;
    private String novaSenha;
    
}
