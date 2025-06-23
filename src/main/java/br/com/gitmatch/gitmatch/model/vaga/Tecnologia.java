package br.com.gitmatch.gitmatch.model.vaga;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "tecnologias")
@Data
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

    
}
