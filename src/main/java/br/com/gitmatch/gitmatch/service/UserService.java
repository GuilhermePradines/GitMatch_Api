package br.com.gitmatch.gitmatch.service;

import br.com.gitmatch.gitmatch.dto.UserRequestDTO;
import br.com.gitmatch.gitmatch.model.User;
import br.com.gitmatch.gitmatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User cadastrarUsuario(UserRequestDTO dto) {
        User user = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .githubUsername(dto.getGithubUsername())
                .build();

        return userRepository.save(user);
    }
}
