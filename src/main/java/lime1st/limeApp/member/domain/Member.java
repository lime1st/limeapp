package lime1st.limeApp.member.domain;

import lime1st.limeApp.member.domain.vo.MemberId;
import lime1st.limeApp.member.domain.vo.MemberRole;

import java.time.LocalDateTime;

/**
 * Domain Entity
 * 가입 시 ID 생성,
 * TODO: 프로필 사진, 리트코드의 프로필을 참고하여 데이터 추가
 **/
public class Member {

//    JpaEntity 로 매핑할 때(실제로는 MemberServiceDTO) 직접 데이터를 변환하므로 JpaEntity 에서 @Embedded 를 적용하지 않았다.
    private final MemberId memberId;
    private final String email;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final MemberRole role;

//    날짜 관련은 JpaAuditing 으로 처리하므로 다른 구현체에는 적용이 안 된다.
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Member create(String email, String username, String password) {
//        사용자 가입 시 기본 값 설정
//        새로 생성할 때는 email, username, password 만 필요하다. 생성 시 create 메서드를 호출해야 memberId 생성
//        memberId(UUID), enable, role...
        return new Builder()
                .memberId(MemberId.withoutId())
                .email(email)
                .username(username)
                .password(password)
                .enabled(false)
                .role(MemberRole.USER)
                .build();
    }

    /**
     * 불변 값의 수정자는 새로운 객체를 반환한다는 의미를 담아 전치사 with 로 메서드 이름을 시작한다.
     **/
    public Member withUpdate(Member member) {
        return new Builder()
                .memberId(member.getMemberId())
                .email("change")
                .username(member.getUsername())
                .password(member.getPassword())
                .enabled(member.isEnabled())
                .role(member.getRole())
                .build();
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MemberRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;
        return getMemberId().equals(member.getMemberId());
    }

    @Override
    public int hashCode() {
        return getMemberId().hashCode();
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }


    /*
    Builder
     */

    // Private 생성자
    private Member(Builder builder) {
        this.memberId = builder.memberId;
        this.email = builder.email;
        this.username = builder.username;
        this.password = builder.password;
        this.enabled = builder.enabled;
        this.role = builder.role;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Static inner builder class
    public static class Builder {
        private MemberId memberId;
        private String email;
        private String username;
        private String password;
        private boolean enabled;
        private MemberRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder memberId(MemberId memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder role(MemberRole role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        // Build method
        public Member build() {
            return new Member(this);
        }
    }
}