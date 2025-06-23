package br.com.gitmatch.gitmatch.controller.vaga;

import br.com.gitmatch.gitmatch.dto.vaga.*;
import br.com.gitmatch.gitmatch.service.vaga.VagaService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vaga")
@CrossOrigin(origins = "*")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @PostMapping("/criar")
    public ResponseEntity<VagaDetalhesDTO> criarVaga(@RequestBody VagaDTO dto, Authentication auth) {
        
        String email = auth.getName();
        Long idEmpresa = vagaService.getUsuarioIdByEmail(email); 

        VagaDetalhesDTO vagaCriada = vagaService.criarVaga(idEmpresa, dto);
        return ResponseEntity.ok(vagaCriada);
    }

    @GetMapping("/empresa")
    public ResponseEntity<List<VagaDetalhesDTO>> listarVagasEmpresa(Authentication auth) {
        String email = auth.getName();
        Long idEmpresa = vagaService.getUsuarioIdByEmail(email);

        List<VagaDetalhesDTO> vagas = vagaService.listarVagasEmpresa(idEmpresa);
        return ResponseEntity.ok(vagas);
    }

    @PostMapping("/candidatar")
    public ResponseEntity<CandidaturaDetalhesDTO> candidatar(@RequestBody CandidaturaDTO dto, Authentication auth) {
        String email = auth.getName();
        Long idUsuario = vagaService.getUsuarioIdByEmail(email);

        // Aqui o percentualCompatibilidade deve vir da lógica de matching da API do GitHub (a implementar)
        Double percentualCompatibilidade = 85.0; // Exemplo estático

        CandidaturaDetalhesDTO candidatura = vagaService.candidatar(idUsuario, dto.getIdVaga(), percentualCompatibilidade);
        return ResponseEntity.ok(candidatura);
    }

}
