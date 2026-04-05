package flowershop.controller;

import flowershop.dto.AuthResponse;
import flowershop.dto.LoginRequest;
import flowershop.dto.RegistrationRequest;
import flowershop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Авторизация", description = "Методы для входа, регистрации и управления аккаунтом")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Вход в систему", description = "Проверяет логин/пароль и возвращает данные пользователя")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.authenticate(request);
    }

    @Operation(summary = "Регистрация", description = "Создает нового пользователя и связанный профиль покупателя")
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegistrationRequest request) {
        return userService.register(request);
    }

    @Operation(summary = "Удаление аккаунта", description = "Удаляет пользователя и его профиль по ID")
    @DeleteMapping("/delete/{id}")
    public void deleteAccount(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}