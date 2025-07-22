package br.com.gitmatch.gitmatch.dto.usuario;

import lombok.Data;

@Data
public class GitHubAuthRequest {
    private String firebaseUid;
    private String nome;
    private String email;
    private String githubUsername;
    // getters e setters
}
