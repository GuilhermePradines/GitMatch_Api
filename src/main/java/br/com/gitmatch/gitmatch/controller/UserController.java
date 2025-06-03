package br.com.gitmatch.gitmatch.controller;

import br.com.gitmatch.gitmatch.dto.UserRequestDTO;
import br.com.gitmatch.gitmatch.model.User;
import br.com.gitmatch.gitmatch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/cadastro")
    public ResponseEntity<User> cadastrar(@RequestBody UserRequestDTO dto) {
        User user = userService.cadastrarUsuario(dto);
        return ResponseEntity.ok(user);
    }
}
