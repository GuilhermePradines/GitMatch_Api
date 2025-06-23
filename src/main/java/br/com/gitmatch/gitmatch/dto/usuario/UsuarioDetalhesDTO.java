package br.com.gitmatch.gitmatch.dto.usuario;

import br.com.gitmatch.gitmatch.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDetalhesDTO {
    private Long idUsuario;
    private String nome;
    private String email;
    private TipoUsuario tipoUsuario;
    private String githubUsername;
    private String profissao;
    private String bio;
    private String fotoPerfil;
    private String cnpj;
}
