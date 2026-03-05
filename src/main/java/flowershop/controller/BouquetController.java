package flowershop.controller;

import flowershop.dto.BouquetDto;
import flowershop.service.BouquetService;
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
@RequestMapping("/bouquets")
@RequiredArgsConstructor
public class BouquetController {

    private final BouquetService bouquetService;


    @GetMapping("/test-nooptimized")
    public List<BouquetDto> findAllWithNPlusOne() {
        return bouquetService.findAll();
    }


    @GetMapping("/test-optimized")
    public List<BouquetDto> findAllOptimized() {
        return bouquetService.findAllOptimized();
    }

    @GetMapping
    public List<BouquetDto> findAll() {
        return bouquetService.findAll();
    }


    @GetMapping("/active")
    public List<BouquetDto> findAllActive() {
        return bouquetService.findAllActive();
    }


    @GetMapping("/{id}")
    public BouquetDto findById(@PathVariable Long id) {
        return bouquetService.findById(id);
    }


    @PostMapping
    public BouquetDto create(@RequestBody BouquetDto dto) {
        return bouquetService.create(dto);
    }


    @PutMapping("/{id}")
    public BouquetDto update(@PathVariable Long id, @RequestBody BouquetDto dto) {
        return bouquetService.update(id, dto);
    }


    @PatchMapping("/{id}/status")
    public BouquetDto updateStatus(@PathVariable Long id, @RequestParam boolean active) {
        return bouquetService.updateStatus(id, active);
    }
}