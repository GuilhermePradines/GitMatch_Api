package br.com.gitmatch.gitmatch.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidato")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_candidato")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Column(name = "github_username", unique = true)
    private String githubUsername;

    private String biografia;
    private String localizacao;
}