package javax.security.authentication.mechanism.http.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.resource.spi.AuthenticationMechanism;

/**
 * Annotation used to define a container {@link AuthenticationMechanism} that implements
 * the HTTP basic access authentication protocol and make that implementation available
 * as an enabled CDI bean.
 * 
 * @author Arjan Tijms
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface BasicAuthenticationMechanismDefinition {
    
    /**
     * Name of realm that will be send via the <code>WWW-Authenticate</code> header.
     * <p>
     * Note that contrary to what happens in some proprietary Servlet products, this
     * realm name <b>does not</b> couple a named identity store configuration to the 
     * authentication mechanism.  
     */
    String realmName() default "";
}
