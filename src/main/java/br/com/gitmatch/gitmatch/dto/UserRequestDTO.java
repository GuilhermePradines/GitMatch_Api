package br.com.gitmatch.gitmatch.dto;

import br.com.gitmatch.gitmatch.model.User;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String nome;
    private String email;
    private String senha;
    private User.TipoUsuario tipoUsuario; // CANDIDATO or EMPRESA
    private String githubUsername;

    private String nomeEmpresa;
    private String setor;
    private String descricao;
}
