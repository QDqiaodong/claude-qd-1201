package com.shoe.care.member;

import com.shoe.care.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickups")
public class PickupController {

    private final PickupService service;

    public PickupController(PickupService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Pickup>> list(@RequestParam(required = false) Long memberId) {
        return ApiResponse.success(service.list(memberId));
    }

    @PostMapping
    public ApiResponse<Pickup> create(@RequestParam Long memberId,
                                      @RequestParam Long leatherId,
                                      @RequestParam Double amount) {
        return ApiResponse.success(service.create(memberId, leatherId, amount));
    }
}
