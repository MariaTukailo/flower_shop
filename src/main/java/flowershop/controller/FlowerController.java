package flowershop.controller;

import flowershop.dto.FlowerDto;
import flowershop.service.FlowerService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/flowers")
@RestController
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;

    @GetMapping("/{id}")
    public FlowerDto getFlowerByCatalogNumber(@PathVariable int id) {
        return flowerService.findFlowerByCatalogNumber(id);
    }

    @GetMapping
    public List<FlowerDto> getFlowersByColor(@RequestParam(required = false) String color) {
        return flowerService.findFlowersByColor(color);
    }
}
