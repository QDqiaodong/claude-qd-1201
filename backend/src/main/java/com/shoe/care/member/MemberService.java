package com.shoe.care.member;

import com.shoe.care.common.BizException;
import com.shoe.care.common.Dict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repo;

    public MemberService(MemberRepository repo) {
        this.repo = repo;
    }

    public List<Member> list() {
        return repo.findAll();
    }

    public Member create(Member in) {
        Member m = new Member();
        m.name = (in.name == null || in.name.isBlank()) ? "会员" : in.name;
        m.phone = (in.phone == null || in.phone.isBlank()) ? "" : in.phone;
        m.points = (in.points == null) ? 0 : in.points;
        m.level = Dict.computeLevel(m.points);
        m.createdAt = LocalDateTime.now();
        return repo.save(m);
    }

    public Member update(Long id, Member in) {
        Member m = repo.findById(id).orElseThrow(() -> new BizException("会员不存在"));
        if (in.name != null && !in.name.isBlank()) {
            m.name = in.name;
        }
        if (in.phone != null) {
            m.phone = in.phone;
        }
        if (in.points != null) {
            m.points = in.points;
            m.level = Dict.computeLevel(m.points);
        }
        return repo.save(m);
    }
}
