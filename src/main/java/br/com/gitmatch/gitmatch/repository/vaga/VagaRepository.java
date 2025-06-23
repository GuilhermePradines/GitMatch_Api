package br.com.gitmatch.gitmatch.repository.vaga;

import br.com.gitmatch.gitmatch.model.vaga.Vaga;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByEmpresa(Usuario empresa);
}
