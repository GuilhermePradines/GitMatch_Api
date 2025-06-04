package br.com.gitmatch.gitmatch.service;
import java.util.List;
import br.com.gitmatch.gitmatch.dto.UserRequestDTO;
import br.com.gitmatch.gitmatch.model.Candidato;
import br.com.gitmatch.gitmatch.model.Empresa;
import br.com.gitmatch.gitmatch.model.User;
import br.com.gitmatch.gitmatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.gitmatch.gitmatch.repository.CandidatoRepository;
import br.com.gitmatch.gitmatch.repository.EmpresaRepository;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User cadastrarUsuario(UserRequestDTO dto) {
        User.TipoUsuario tipo = dto.getTipoUsuario(); // Enum diretamente
        User user = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .githubUsername(dto.getGithubUsername())
                .tipoUsuario(tipo)
                .build();

        userRepository.save(user); // salva usuário primeiro

        if (tipo == User.TipoUsuario.CANDIDATO) {
            Candidato candidato = new Candidato();
            candidato.setUsuario(user);
            candidato.setGithubUsername(dto.getGithubUsername());
            candidatoRepository.save(candidato);
        } else if (tipo == User.TipoUsuario.EMPRESA) {
            Empresa empresa = new Empresa();
            empresa.setUsuario(user);
            empresa.setNomeEmpresa(dto.getNomeEmpresa()); // adapte conforme DTO
            empresa.setSetor(dto.getSetor());
            empresa.setDescricao(dto.getDescricao());
            empresaRepository.save(empresa);
        } else {
            throw new IllegalArgumentException("Tipo de usuário inválido");
        }

        return user;
    }

     
   

    public List<User> listarTodos() {
        return userRepository.findAll();
}
}
