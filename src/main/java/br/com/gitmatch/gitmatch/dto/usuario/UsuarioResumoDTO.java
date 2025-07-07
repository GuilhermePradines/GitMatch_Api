package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResumoDTO {
    private String nome;
    private String profissao;
    private String fotoPerfil;
}
