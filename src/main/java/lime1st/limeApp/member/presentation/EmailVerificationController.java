package lime1st.limeApp.member.presentation;

import lime1st.limeApp.member.application.EmailVerificationService;
import lime1st.limeApp.member.application.MemberService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

/*
* 가입한 회원의 이메일 인증을 위한 컨트롤러
* */
@RestController
public class EmailVerificationController {

    private final EmailVerificationService service;
    private final MemberService memberService;

    public EmailVerificationController(EmailVerificationService service, MemberService memberService) {
        this.service = service;
        this.memberService = memberService;
    }

    @GetMapping("/email-verify")
    public String verifyEmail(@RequestParam("id") String id) {

        byte[] actualId = Base64.getDecoder().decode(id.getBytes());
        String email = service.getEmailForVerificationId(new String(actualId));

        if (email != null) {
            var member = memberService.findByEmail(email);

//            이메일 인증이 되어야 계정이 활성화 된다.
//            enabled 속성은 security 에서 활성화 여부를 확인하는데 사용하고 있다.
//            비활성화 상태에서는 로그인이 안 된다.
            //  TODO: enabled 속성은 도메인이다...
//            member.setEnabled(true);  도메인에서 수정하도록 변경해보자
//            memberService.update(member, member.username());

            return "이메일 확인이 완료되었습니다.";
        }

        return "잘못된 경로 요청입니다.";
    }
}
