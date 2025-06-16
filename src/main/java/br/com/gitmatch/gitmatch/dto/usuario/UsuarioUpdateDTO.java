package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.Data;

@Data
public class UsuarioUpdateDTO {
    private String nome;
    private String email;
    private String profissao;  // descrição para empresas
    private String bio;
    private String fotoPerfil; 
    private String githubUsername; // se candidato
    private String cnpj; // se empresa
}
