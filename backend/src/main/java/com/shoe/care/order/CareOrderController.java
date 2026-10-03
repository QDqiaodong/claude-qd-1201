package com.shoe.care.order;

import com.shoe.care.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/care-orders")
public class CareOrderController {

    private final CareOrderService service;

    public CareOrderController(CareOrderService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<CareOrder>> list(@RequestParam(required = false) Long leatherId) {
        return ApiResponse.success(service.list(leatherId));
    }

    @PostMapping
    public ApiResponse<CareOrder> create(@RequestBody CareOrder in) {
        return ApiResponse.success(service.create(in));
    }

    @PutMapping("/{id}")
    public ApiResponse<CareOrder> update(@PathVariable Long id, @RequestBody CareOrder in) {
        return ApiResponse.success(service.update(id, in));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<CareOrder> complete(@PathVariable Long id) {
        return ApiResponse.success(service.complete(id));
    }
}
