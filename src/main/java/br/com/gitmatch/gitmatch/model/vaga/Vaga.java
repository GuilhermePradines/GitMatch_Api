package br.com.gitmatch.gitmatch.model.vaga;

import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "vagas")
@Getter
@Setter
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

    @Column(length = 100)
    private String localizacao; 

    @Column(length = 50)
    private String turno;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vaga)) return false;
        Vaga vaga = (Vaga) o;
        return idVaga != null && idVaga.equals(vaga.idVaga);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVaga);
    }
}