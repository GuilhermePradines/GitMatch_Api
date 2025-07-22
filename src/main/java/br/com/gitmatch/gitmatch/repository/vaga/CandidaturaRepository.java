package br.com.gitmatch.gitmatch.repository.vaga;

import br.com.gitmatch.gitmatch.model.vaga.Candidatura;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.model.vaga.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    Optional<Candidatura> findByCandidatoAndVaga(Usuario candidato, Vaga vaga);
    List<Candidatura> findByVaga(Vaga vaga);
    List<Candidatura> findByCandidato(Usuario candidato);
    @Query("SELECT c.vaga.idVaga FROM Candidatura c WHERE c.candidato.id = :usuarioId")
    List<Long> findVagaIdsByUsuarioId(Long usuarioId);

    boolean existsByCandidatoAndVaga(Usuario candidato, Vaga vaga);
    void deleteAllByCandidato(Usuario candidato);
    void deleteAllByVaga(Vaga vaga);
}
