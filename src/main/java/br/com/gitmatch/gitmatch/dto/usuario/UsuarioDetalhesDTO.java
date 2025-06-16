package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDetalhesDTO {
    private Long idUsuario;
    private String nome;
    private String email;
    private String tipoUsuario;
    private String githubUsername;
    private String profissao;
    private String bio;
    private String fotoPerfil;
    private String cnpj;
}
