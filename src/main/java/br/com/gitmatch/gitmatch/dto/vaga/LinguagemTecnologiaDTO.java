package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinguagemTecnologiaDTO {
 
    private String nome;
    private long bytes;
}