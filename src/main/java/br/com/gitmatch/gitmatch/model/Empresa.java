package br.com.gitmatch.gitmatch.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "empresa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Column(name = "nome_empresa")
    private String nomeEmpresa;

    private String setor;
    private String descricao;
}

