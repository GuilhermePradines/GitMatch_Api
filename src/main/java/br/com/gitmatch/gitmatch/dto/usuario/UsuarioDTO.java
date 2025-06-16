package br.com.gitmatch.gitmatch.dto.usuario;


import lombok.Data;

@Data
public class UsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private String tipoUsuario; // "candidato" ou "empresa"
    private String githubUsername; // se candidato
    private String cnpj; // se empres
}