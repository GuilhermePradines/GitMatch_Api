package br.com.gitmatch.gitmatch.repository;


import br.com.gitmatch.gitmatch.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {}