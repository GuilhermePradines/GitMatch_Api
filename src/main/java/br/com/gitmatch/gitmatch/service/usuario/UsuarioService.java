package br.com.gitmatch.gitmatch.service.usuario;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import br.com.gitmatch.gitmatch.dto.usuario.LoginDTO;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioDTO;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResponseDTO;
import br.com.gitmatch.gitmatch.enums.TipoUsuario;
import br.com.gitmatch.gitmatch.model.usuario.PasswordResetToken;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.repository.usuario.PasswordResetRepository;
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

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetRepository resetRepo;

    public UsuarioResponseDTO cadastrar(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenhaHash(encoder.encode(dto.getSenha()));
        usuario.setTipoUsuario(dto.getTipoUsuario()); 

        if (dto.getTipoUsuario() == TipoUsuario.CANDIDATO) {
            usuario.setGithubUsername(dto.getGithubUsername());
        } else if (dto.getTipoUsuario() == TipoUsuario.EMPRESA) {
            usuario.setCnpj(dto.getCnpj());
        }

        usuario.setTermosAceitos(true);
        usuarioRepo.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail());

        return new UsuarioResponseDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario().name(),
                token
        );
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

        return new UsuarioResponseDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario().name(),
                token
        );
    }

    public void alterarSenha(String email, String senhaAtual, String novaSenha) {
    Usuario usuario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    if (!encoder.matches(senhaAtual, usuario.getSenhaHash())) {
        throw new RuntimeException("Senha atual incorreta.");
    }

    usuario.setSenhaHash(encoder.encode(novaSenha));
    usuarioRepo.save(usuario);
}




    @Transactional
public void enviarCodigoRecuperacao(String email) {
    Usuario usuario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email não registrado."));

    String token = gerarTokenAleatorio();
    resetRepo.deleteByEmail(email);  

    PasswordResetToken reset = new PasswordResetToken();
    reset.setEmail(email);
    reset.setToken(token);
    resetRepo.save(reset);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Código de recuperação de senha");
    message.setText("Seu código é: " + token);
    mailSender.send(message);
}

@Transactional
public void redefinirSenha(String email, String novaSenha) {
    Usuario usuario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    usuario.setSenhaHash(encoder.encode(novaSenha));
    usuarioRepo.save(usuario);
    resetRepo.deleteByEmail(email);  
}

private String gerarTokenAleatorio() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder();
    Random rand = new Random();
    for (int i = 0; i < 6; i++) {
        sb.append(chars.charAt(rand.nextInt(chars.length())));
    }
    return sb.toString();
}
@Transactional(readOnly = true)
public void verificarCodigo(String email, String token) {
    resetRepo.findByEmailAndToken(email, token)
        .orElseThrow(() -> new RuntimeException("Código inválido ou expirado."));
}


}
