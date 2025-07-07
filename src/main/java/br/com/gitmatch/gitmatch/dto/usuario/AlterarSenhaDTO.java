package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.Data;

@Data
public class AlterarSenhaDTO {
    private String senhaAtual;
    private String novaSenha;
}