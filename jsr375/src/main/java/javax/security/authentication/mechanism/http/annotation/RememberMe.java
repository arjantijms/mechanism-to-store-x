package javax.security.authentication.mechanism.http.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.el.ELProcessor;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import javax.resource.spi.AuthenticationMechanism;
import javax.security.identitystore.RememberMeIdentityStore;
import javax.servlet.http.Cookie;

/**
 * The RememberMe annotation provides an application the ability to declarative designate 
 * that an {@link AuthenticationMechanism} "remembers" the authentication and auto
 * applies this with every request.
 * 
 * <p>
 * The location where this authentication is remembered has to be set by enabling a bean in
 * the application that implements the {@link RememberMeIdentityStore} interface.
 * 
 * <p>
 * For the remember me function the credentials provided by the caller are exchanged for a token
 * which is send to the user as the value of a cookie, in a similar way to how the HTTP session ID is send.
 * It should be realized that this token effectively becomes the credential to establish the caller's
 * identity within the application and care should be taken to handle and store the token securely. E.g.
 * by using this feature with a secure transport (SSL/https), storing a strong hash instead of the actual
 * token, and implementing an expiration policy. 
 * 
 * <p>
 * This support is provided via an implementation of an interceptor spec interceptor that conducts the
 * necessary logic.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * <code>
 *     {@literal @}RequestScoped
 *     {@literal @}RememberMe
 *     public class CustomAuthenticationMechanism implements HttpAuthenticationMechanism {
 *         // ...
 *     }
 * </code>
 * </pre>
 * 
 * @author Arjan Tijms
 *
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target(TYPE)
public @interface RememberMe {
    
    /**
     * Max age in seconds for the remember me cookie.
     * Defaults to one day.
     * 
     * @see Cookie#setMaxAge(int)
     * 
     */
    @Nonbinding
    int cookieMaxAgeSeconds() default 86400; // 1 day
    
    /**
     * EL expression to determine if remember me should be used. This is evaluated
     * for every request requiring authentication. The expression needs to evaluate
     * to a boolean outcome. All named CDI beans are available to the expression
     * as well as default classes as specified by EL 3.0 for the {@link ELProcessor}
     * and the implicit object "this" which refers to the interceptor target.
     * 
     */
    @Nonbinding
    String isRememberMeExpression() default "";
}