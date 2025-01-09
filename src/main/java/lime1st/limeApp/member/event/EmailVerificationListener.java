package lime1st.limeApp.member.event;

import lime1st.limeApp.member.application.EmailVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EmailVerificationListener implements ApplicationListener<MemberRegistrationEvent> {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationListener.class);

    private final JavaMailSender javaMailSender;
    private final EmailVerificationService emailVerificationService;

    public EmailVerificationListener(JavaMailSender javaMailSender,
                                     EmailVerificationService emailVerificationService) {
        this.javaMailSender = javaMailSender;
        this.emailVerificationService = emailVerificationService;
    }

    public void onApplicationEvent(MemberRegistrationEvent event) {
        var username = event.getUsername();
        var email = event.getEmail();
        var verificationId = emailVerificationService.getVerificationIdByEmail(email);

        var message = new SimpleMailMessage();
        message.setSubject("계정 확인 메일입니다.");
        message.setText(getText(username, verificationId));
        message.setTo(email);
        javaMailSender.send(message);
    }

    private String getText(String username, String verificationId) {
        var encodedVerificationId = new String(Base64.getEncoder().encode(verificationId.getBytes()));

        return "to " + username + "," + System.lineSeparator() + System.lineSeparator()
                + "LimeApp 에서 귀하의 계정이 성공적으로 생성되었습니다. "
                + "다음 링크를 클릭하여 계정을 활성화하세요." + System.lineSeparator()
                + "http://localhost:8080/email-verify?id=" + encodedVerificationId
                + System.lineSeparator() + System.lineSeparator()
                + "from" + System.lineSeparator() + "MyApp";
    }
}
