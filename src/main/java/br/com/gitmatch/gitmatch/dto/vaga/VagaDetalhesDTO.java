package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class VagaDetalhesDTO {
    private Long idVaga;
    private String titulo;
    private String descricao;
    private String areaAtuacao;
    private LocalDateTime dataCriacao;
    private boolean ativo;
    private Set<String> tecnologias;
    private Long idEmpresa;
}
