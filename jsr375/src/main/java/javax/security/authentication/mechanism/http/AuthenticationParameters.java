package javax.security.authentication.mechanism.http;

import javax.servlet.http.HttpServletRequest;

/**
 * Mockup for a series of parameters that could potentially
 * be provided alongside a call to 
 * {@link HttpServletRequest#authenticate(javax.servlet.http.HttpServletResponse)}
 * 
 * <p>
 * Status: This is an early draft of a possible API and has not been extensively
 * discussed yet.
 * 
 * @author Arjan Tijms
 *
 */
public interface AuthenticationParameters {

    AuthenticationParameters username(String username);
    AuthenticationParameters password(String passWord);
    AuthenticationParameters rememberMe(boolean rememberMe);
    AuthenticationParameters noPassword(boolean noPassword);
    AuthenticationParameters authMethod(String authMethod);
    AuthenticationParameters redirectUrl(String redirectUrl);

    String getUsername();
    void setUsername(String username);

    String getPassword();
    void setPassword(String password);

    Boolean getRememberMe();
    void setRememberMe(Boolean rememberMe);

    String getAuthMethod();
    void setAuthMethod(String authMethod);

    String getRedirectUrl();
    void setRedirectUrl(String redirectUrl);

    Boolean getNoPassword();
    void setNoPassword(Boolean noPassword);

}