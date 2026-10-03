package com.shoe.care.part;

import com.shoe.care.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService service;

    public PartController(PartService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Part>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/uses")
    public ApiResponse<List<PartUse>> listUses() {
        return ApiResponse.success(service.listUses());
    }

    @PostMapping
    public ApiResponse<Part> create(@RequestBody Part in) {
        return ApiResponse.success(service.create(in));
    }

    @PutMapping("/{id}")
    public ApiResponse<Part> update(@PathVariable Long id, @RequestBody Part in) {
        return ApiResponse.success(service.update(id, in));
    }

    @PostMapping("/{id}/pickup")
    public ApiResponse<PartUse> pickup(@PathVariable Long id, @RequestParam Integer qty) {
        return ApiResponse.success(service.pickup(id, qty));
    }
}
