package flowershop;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Flower Shop "
        )
)
public class FlowerShopApplication {

    public static void main(String[] args) {

        SpringApplication.run(FlowerShopApplication.class, args);
    }
}
