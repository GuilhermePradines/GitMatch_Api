package br.com.gitmatch.gitmatch.repository.usuario;
import java.util.List;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
     List<Usuario> findByTipoUsuario(String tipoUsuario);
}