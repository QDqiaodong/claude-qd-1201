package com.shoe.care.member;

import com.shoe.care.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Member>> list() {
        return ApiResponse.success(service.list());
    }

    @PostMapping
    public ApiResponse<Member> create(@RequestBody Member in) {
        return ApiResponse.success(service.create(in));
    }

    @PutMapping("/{id}")
    public ApiResponse<Member> update(@PathVariable Long id, @RequestBody Member in) {
        return ApiResponse.success(service.update(id, in));
    }
}
