package br.com.gitmatch.gitmatch.dto.usuario;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long idUsuario;
    private String nome;
    private String email;
    private String tipoUsuario;
    private String token;
}
