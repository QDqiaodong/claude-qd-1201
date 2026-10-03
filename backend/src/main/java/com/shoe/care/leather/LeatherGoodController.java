package com.shoe.care.leather;

import com.shoe.care.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leather-goods")
public class LeatherGoodController {

    private final LeatherGoodService service;

    public LeatherGoodController(LeatherGoodService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<LeatherGood>> list() {
        return ApiResponse.success(service.list());
    }

    @PostMapping
    public ApiResponse<LeatherGood> create(@RequestBody LeatherGood in) {
        return ApiResponse.success(service.create(in));
    }

    @PutMapping("/{id}")
    public ApiResponse<LeatherGood> update(@PathVariable Long id, @RequestBody LeatherGood in) {
        return ApiResponse.success(service.update(id, in));
    }

    @PostMapping("/{id}/status")
    public ApiResponse<LeatherGood> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(service.updateStatus(id, status));
    }
}
