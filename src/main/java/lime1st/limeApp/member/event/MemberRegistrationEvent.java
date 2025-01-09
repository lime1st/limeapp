package lime1st.limeApp.member.event;

import org.springframework.context.ApplicationEvent;

import java.io.Serial;

public class MemberRegistrationEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -2685172945219633123L;

    private final String username;
    private final String email;

    public MemberRegistrationEvent(String username, String email) {
        super(username);
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
