package br.com.gitmatch.gitmatch.service.usuario;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.gitmatch.gitmatch.dto.usuario.LoginDTO;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioDTO;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResponseDTO;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.security.JwtUtil;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UsuarioResponseDTO cadastrar(UsuarioDTO dto) {
    Usuario usuario = new Usuario();
    usuario.setNome(dto.getNome());
    usuario.setEmail(dto.getEmail());
    usuario.setSenhaHash(encoder.encode(dto.getSenha()));
    usuario.setTipoUsuario(dto.getTipoUsuario());

    if ("candidato".equalsIgnoreCase(dto.getTipoUsuario())) {
        usuario.setGithubUsername(dto.getGithubUsername());
    } else if ("empresa".equalsIgnoreCase(dto.getTipoUsuario())) {
        usuario.setCnpj(dto.getCnpj());
    }

    usuario.setTermosAceitos(true); 
    usuarioRepo.save(usuario);

    String token = jwtUtil.generateToken(usuario.getEmail());

    return new UsuarioResponseDTO(usuario.getNome(), usuario.getEmail(), usuario.getTipoUsuario(), token);
}


    public UsuarioResponseDTO login(LoginDTO dto) throws Exception {
        Usuario usuario = usuarioRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new Exception("Usuário não encontrado"));

        if (!encoder.matches(dto.getSenha(), usuario.getSenhaHash())) {
            throw new Exception("Senha inválida");
        }

        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepo.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail());

        return new UsuarioResponseDTO(usuario.getNome(), usuario.getEmail(), usuario.getTipoUsuario(), token);
    }
}
