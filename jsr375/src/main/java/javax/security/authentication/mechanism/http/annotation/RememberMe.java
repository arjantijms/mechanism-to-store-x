package javax.security.authentication.mechanism.http.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.el.ELProcessor;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import javax.servlet.http.Cookie;

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