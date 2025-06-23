package br.com.gitmatch.gitmatch.model.vaga;

import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "vagas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVaga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Usuario empresa;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String areaAtuacao;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    private boolean ativo = true;

    @ManyToMany
    @JoinTable(
        name = "vaga_tecnologias",
        joinColumns = @JoinColumn(name = "id_vaga"),
        inverseJoinColumns = @JoinColumn(name = "id_tecnologia")
    )
    private Set<Tecnologia> tecnologias;

    
}
