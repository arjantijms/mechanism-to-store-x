package javax.security.identitystore;

import java.util.List;

import javax.security.CallerPrincipal;
import javax.security.identitystore.credential.RememberMeCredential;

public interface RememberMeIdentityStore {

    CredentialValidationResult validate(RememberMeCredential credential);
    
    String generateLoginToken(CallerPrincipal callerPrincipal, List<String> groups);
    void removeLoginToken(String token);
    
}
