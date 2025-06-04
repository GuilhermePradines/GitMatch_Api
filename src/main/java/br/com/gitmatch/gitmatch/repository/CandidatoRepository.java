package br.com.gitmatch.gitmatch.repository;

import br.com.gitmatch.gitmatch.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {}