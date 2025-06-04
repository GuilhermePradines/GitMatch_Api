package br.com.gitmatch.gitmatch.controller;
import java.util.List;
import br.com.gitmatch.gitmatch.dto.UserRequestDTO;
import br.com.gitmatch.gitmatch.model.User;
import br.com.gitmatch.gitmatch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/cadastro")
   public ResponseEntity<String> cadastrar(@RequestBody UserRequestDTO dto) {
    try {
        userService.cadastrarUsuario(dto);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
    }
   }
    @GetMapping
    public ResponseEntity<List<User>> listarTodos() {
        return ResponseEntity.ok(userService.listarTodos());
    }
}
