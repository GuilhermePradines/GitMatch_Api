package br.com.gitmatch.gitmatch.controller.email;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import br.com.gitmatch.gitmatch.dto.email.EmailCodigoDTO;

import br.com.gitmatch.gitmatch.dto.email.EmailRequest;
import br.com.gitmatch.gitmatch.dto.email.TrocaSenhaDTO;
import br.com.gitmatch.gitmatch.model.usuario.PasswordResetToken;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.repository.usuario.PasswordResetRepository;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.service.email.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetRepository tokenRepository;

    

   @Autowired
private UsuarioRepository usuarioRepository; // já existe né?

@PostMapping("/enviar-codigo")
public ResponseEntity<?> enviarCodigo(@RequestBody EmailRequest dto) {
    String email = dto.getTo();
System.out.println("Email recebido para envio: " + email);  
    if (!usuarioRepository.existsByEmail(email)) {
        return ResponseEntity.status(404).body("Usuário não encontrado");
    }
    

    String token = gerarCodigo();

    PasswordResetToken resetToken = tokenRepository.findByEmail(email)
        .orElse(new PasswordResetToken());

    resetToken.setEmail(email);
    resetToken.setToken(token);
    resetToken.setCriadoEm(LocalDateTime.now());
    tokenRepository.save(resetToken);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Seu código de verificação");
    message.setText("Seu código é: " + token);
    message.setFrom("gitmatch4@gmail.com"); // já configurado via @Value
    mailSender.send(message);

    return ResponseEntity.ok("Código enviado para: " + email);
}


    private String gerarCodigo() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); // 6 dígitos
        return String.valueOf(code);
    }

@PostMapping("/validar-codigo")
public ResponseEntity<?> validarCodigo(@RequestBody EmailCodigoDTO dto) {
    String email = dto.getEmail();
    String codigo = dto.getCodigo();

    PasswordResetToken token = tokenRepository.findByEmail(email)
        .orElse(null);

    if (token == null || !token.getToken().equals(codigo)) {
        return ResponseEntity.status(400).body("Código inválido");
    }

    // Verifica se expirou (10 min de validade, por exemplo)
    if (token.getCriadoEm().plusMinutes(10).isBefore(LocalDateTime.now())) {
        return ResponseEntity.status(400).body("Código expirado");
    }

    return ResponseEntity.ok("Código válido");
}


@PostMapping("/trocar-senha")
public ResponseEntity<?> trocarSenha(@RequestBody TrocaSenhaDTO dto) {
    String email = dto.getEmail();
    String codigo = dto.getCodigo();
    String novaSenha = dto.getNovaSenha();

    PasswordResetToken token = tokenRepository.findByEmail(email)
        .orElse(null);

    if (token == null || !token.getToken().equals(codigo)) {
        return ResponseEntity.status(400).body("Código inválido");
    }

    if (token.getCriadoEm().plusMinutes(10).isBefore(LocalDateTime.now())) {
        return ResponseEntity.status(400).body("Código expirado");
    }

    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElse(null);

    if (usuario == null) {
        return ResponseEntity.status(404).body("Usuário não encontrado");
    }

    usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
    usuarioRepository.save(usuario);

    // Invalida o token após uso
    tokenRepository.delete(token);

    return ResponseEntity.ok("Senha atualizada com sucesso");
}




}
