package br.com.gitmatch.gitmatch.repository.usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.gitmatch.gitmatch.model.usuario.PasswordResetToken;

public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmail(String email);
    Optional<PasswordResetToken> findByEmailAndToken(String email, String token);
    void deleteByEmail(String email);
}

