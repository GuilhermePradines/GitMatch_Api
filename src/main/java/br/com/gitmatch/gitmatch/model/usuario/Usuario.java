package br.com.gitmatch.gitmatch.model.usuario;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    private String profissao;

    private String bio;

    private String fotoPerfil;

    @Column(nullable = false)
    private String tipoUsuario; // candidato ou empresa

    @Column(unique = true)
    private String cnpj;

    private String githubUsername;

    private Boolean termosAceitos = false;

    private LocalDateTime criadoEm = LocalDateTime.now();

    private LocalDateTime ultimoLogin;
}