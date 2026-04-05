package flowershop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Данные для регистрации нового пользователя и создания профиля покупателя")
public class RegistrationRequest {

    @Schema(description = "Логин пользователя", example = "flowers")
    private String username;

    @Schema(description = "Пароль", example = "password123")
    private String password;

    @Schema(description = "Имя покупателя (ФИО)", example = "Иван Иванов")
    private String name;

    @Schema(description = "Номер телефона", example = "+375291234567")
    private String phone;
}