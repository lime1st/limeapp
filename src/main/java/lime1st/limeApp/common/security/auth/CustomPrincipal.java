package lime1st.limeApp.common.security.auth;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
public class CustomPrincipal implements Principal {

    private final String memberId;

    @Override
    public String getName() {
        return memberId;
    }
}
