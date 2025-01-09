package lime1st.limeApp.member.application;

import lime1st.limeApp.member.domain.Member;
import lime1st.limeApp.member.domain.vo.MemberId;
import lime1st.limeApp.member.domain.vo.MemberRole;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MemberServiceDTO 는 개발 연습의 편의를 위해 모든 데이터를 가지도록 했다.
 * 나중에 시간이 되면 ControllerDTO 처럼 분리해 보자
* */
public record MemberServiceDTO(
        String memberId,
        String email,
        String username,
        String password,
        boolean enabled,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static MemberServiceDTO fromDomain(Member member) {
        return new MemberServiceDTO(
                member.getMemberId().id().toString(),
                member.getEmail(),
                member.getUsername(),
                member.getPassword(),
                member.isEnabled(),
                member.getRole().toString(),
                member.getCreatedAt(),
                member.getUpdatedAt());
    }

    public Member toDomain() {
        return new Member.Builder()
                .memberId(MemberId.withId(this.memberId))
                .email(this.email)
                .username(this.username)
                .enabled(this.enabled)
                .role(MemberRole.valueOf(this.role))
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
