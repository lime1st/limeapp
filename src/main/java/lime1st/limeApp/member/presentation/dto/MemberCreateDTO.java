package lime1st.limeApp.member.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lime1st.limeApp.member.application.MemberServiceDTO;

/**
 * 회원 가입 시 사용자가 실제 입력하는 값만 사용한다.
 * 각 레이어에서는 보안을 위해 memberId 를 갖고 있지 않는 것이 좋다.
 * 그리고 실제 API 에 필요한 데이터만 갖고 있어야 한다.
 * Create, Retrieve, Update, Delete 에 사용되는(필요한) 데이터가 다르기 때문에
 * dto 를 필요에 따라 다르게 만든다. 단일 책임 원칙을 지키기 위해?
 *
 * TODO: CQRS 로 변경
 */
public record MemberCreateDTO(
        @NotNull(message = "email cannot be null")
        String email,
        @NotNull(message = "username cannot be null")
        String username,
        @NotNull(message = "password cannot be null")
        String password
) {

//    프레젠테이션 계층에서 서비스 계층으로 보낼 때는 이미 인스턴스가 생성되어 있을 것이므로 public 메서드로 만들었다.
public MemberServiceDTO toService() {

        return new MemberServiceDTO(
                null,
                this.email,
                this.username,
                this.password,
                false,
                null,
                null,
                null
        );
    }
}
