package br.com.gitmatch.gitmatch.service.usuario;

import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> User.builder()
                        .username(usuario.getEmail())
                        .password(usuario.getSenhaHash())
                        .roles(usuario.getTipoUsuario().toUpperCase())
                        .build()
                ).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
