package flowershop.controller;

import flowershop.dto.CustomerDto;
import flowershop.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @GetMapping
    public List<CustomerDto> findAll() {
        return customerService.findAll();
    }


    @GetMapping("/{id}")
    public CustomerDto findById(@PathVariable Long id) {
        CustomerDto customerDto = customerService.findById(id);
        if (customerDto == null) {
            return null;
        }
        return customerDto;
    }


    @PostMapping
    public CustomerDto create(@RequestBody CustomerDto dto) {

        return customerService.createTransactional(dto);
    }


    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable Long id, @RequestBody CustomerDto dto) {
        return customerService.update(id, dto);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @PostMapping("/test-no-tx")
    public CustomerDto testNoTx(@RequestBody CustomerDto dto) {
        return customerService.createWithoutTransaction(dto);
    }

    @PostMapping("/test-tx")
    public CustomerDto testTx(@RequestBody CustomerDto dto) {
        return customerService.createWithTransaction(dto);
    }
}