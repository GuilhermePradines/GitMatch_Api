package br.com.gitmatch.gitmatch.model.vaga;

import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidaturas",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"id_usuario", "id_vaga"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario candidato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vaga", nullable = false)
    private Vaga vaga;

    @Column(nullable = false, precision = 5, scale = 2)
    private Double percentualCompatibilidade;

    @Column(nullable = false)
    private LocalDateTime dataCandidatura = LocalDateTime.now();

    
}
