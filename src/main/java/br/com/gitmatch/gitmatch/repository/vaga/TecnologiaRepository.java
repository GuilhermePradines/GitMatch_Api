package br.com.gitmatch.gitmatch.repository.vaga;

import br.com.gitmatch.gitmatch.model.vaga.Tecnologia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TecnologiaRepository extends JpaRepository<Tecnologia, Long> {
    Optional<Tecnologia> findByNome(String nome);
}
