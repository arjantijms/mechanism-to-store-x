package javax.security.authentication.mechanism.http.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;
import javax.resource.spi.AuthenticationMechanism;

/**
 * The AutoApplySession annotation provides an application the ability to declarative designate 
 * that an {@link AuthenticationMechanism} uses the <code>javax.servlet.http.registerSession<code> 
 * and auto applies this for every request.
 * 
 * <p>
 * See the JASPIC specification for further details on <code>javax.servlet.http.registerSession<code>.
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
 *     {@literal @}AutoApplySession
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
public @interface AutoApplySession {
}