package br.com.gitmatch.gitmatch.repository.vaga;

import br.com.gitmatch.gitmatch.model.vaga.Vaga;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByEmpresa(Usuario empresa);
    List<Vaga> findByEmpresaAndAtivoTrue(Usuario empresa);

    @EntityGraph(attributePaths = {"empresa", "tecnologias"})
    List<Vaga> findAll();

    @EntityGraph(attributePaths = {"empresa", "tecnologias"})
    List<Vaga> findByAtivoTrue();

     
    @Query("SELECT v FROM Vaga v LEFT JOIN FETCH v.tecnologias WHERE v.idVaga = :idVaga")
    Optional<Vaga> findByIdWithTecnologias(@Param("idVaga") Long idVaga);

    @Query("SELECT DISTINCT v FROM Vaga v LEFT JOIN FETCH v.tecnologias WHERE v.empresa.idUsuario = :idEmpresa")
    List<Vaga> findByEmpresaWithTecnologias(@Param("idEmpresa") Long idEmpresa);

   
    @Query("SELECT DISTINCT v FROM Vaga v LEFT JOIN FETCH v.tecnologias WHERE v.ativo = true")
    List<Vaga> findByAtivoTrueWithTecnologias();


//     @Query(value = """
//         SELECT v.*
//         FROM vagas v
//         WHERE v.id_vaga IN (
//             SELECT vt.id_vaga
//             FROM vaga_tecnologias vt
//             JOIN tecnologias t ON vt.id_tecnologia = t.id_tecnologia
//             WHERE LOWER(t.nome) LIKE ANY (ARRAY[LOWER(CONCAT('%', :tecNames, '%'))])
//         )
//         AND v.ativo = true
//         """, nativeQuery = true)
        
        
//     List<Vaga> findVagasAtivasPorTecnologias(@Param("tecNames") String[] tecNames);
// 
@Query(value = """
  SELECT DISTINCT v.*
FROM vagas v
JOIN vaga_tecnologias vt ON v.id_vaga = vt.id_vaga
JOIN tecnologias t ON vt.id_tecnologia = t.id_tecnologia
JOIN unnest(:tecNames) AS tech(name) ON LOWER(t.nome) = LOWER(tech.name)
WHERE v.ativo = true

    """, nativeQuery = true)
List<Vaga> findVagasAtivasPorTecnologias(@Param("tecNames") String[] tecNames);


@Query(value = """
    SELECT
        u.nome AS nome_candidato,
        u.profissao AS profissao,
        u.github_username AS github,
        c.percentual_compatibilidade,
        c.data_candidatura
    FROM candidaturas c
    JOIN usuarios u ON c.id_usuario = u.id_usuario
    WHERE c.id_vaga = :idVaga
    """, nativeQuery = true)
List<Object[]> buscarCandidatosDetalhesPorVaga(@Param("idVaga") Long idVaga);

void deleteAllByEmpresa(Usuario empresa);
List<Vaga> findAllByEmpresa(Usuario empresa);

}
