package flowershop.controller;

import flowershop.dto.FlowerDto;
import flowershop.service.FlowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flowers")
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;


    @GetMapping
    public List<FlowerDto> findAll() {
        return flowerService.findAll();
    }

    @GetMapping("/active")
    public List<FlowerDto> findAllActive() {
        return flowerService.findAllActive();
    }


    @GetMapping("/{id}")
    public FlowerDto findById(@PathVariable Long id) {
        return flowerService.findById(id);
    }


    @PostMapping
    public FlowerDto create(@RequestBody FlowerDto dto) {
        return flowerService.create(dto);
    }


    @PutMapping("/{id}")
    public FlowerDto update(@PathVariable Long id, @RequestBody FlowerDto dto) {
        return flowerService.update(id, dto);
    }


    @PatchMapping("/{id}/status")
    public FlowerDto updateStatus(@PathVariable Long id, @RequestParam boolean active) {
        return flowerService.updateStatus(id, active);
    }
}
