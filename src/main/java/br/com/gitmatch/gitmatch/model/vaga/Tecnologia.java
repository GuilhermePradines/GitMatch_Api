package br.com.gitmatch.gitmatch.model.vaga;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "tecnologias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tecnologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTecnologia;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @ManyToMany(mappedBy = "tecnologias")
    private Set<Vaga> vagas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tecnologia)) return false;
        Tecnologia that = (Tecnologia) o;
        return idTecnologia != null && idTecnologia.equals(that.idTecnologia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTecnologia);
    }
}