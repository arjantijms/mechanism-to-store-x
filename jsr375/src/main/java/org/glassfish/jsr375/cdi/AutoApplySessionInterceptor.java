package org.glassfish.jsr375.cdi;

import static java.lang.Boolean.TRUE;
import static javax.interceptor.Interceptor.Priority.PLATFORM_BEFORE;
import static javax.security.auth.message.AuthStatus.SUCCESS;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.authentication.mechanism.http.HttpMessageContext;
import javax.security.authentication.mechanism.http.annotation.AutoApplySession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Interceptor
@AutoApplySession
@Priority(PLATFORM_BEFORE + 200)
public class AutoApplySessionInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final static Method validateRequestMethod = getMethod(
        HttpAuthenticationMechanism.class, 
        "validateRequest",
        HttpServletRequest.class, HttpServletResponse.class, HttpMessageContext.class);

    @SuppressWarnings("unchecked")
    @AroundInvoke
    public Object intercept(InvocationContext invocationContext) throws Exception {
        
        if (isImplementationOf(invocationContext.getMethod(), validateRequestMethod)) {
            
            HttpMessageContext httpMessageContext = (HttpMessageContext)invocationContext.getParameters()[2];
            Principal userPrincipal = httpMessageContext.getRequest().getUserPrincipal();
            
            if (userPrincipal != null) {
                
                httpMessageContext.getHandler().handle(new Callback[] { 
                    new CallerPrincipalCallback(httpMessageContext.getClientSubject(), userPrincipal) }
                );
                         
                return SUCCESS;
            }
            
            Object outcome = invocationContext.proceed();
            
            if (SUCCESS.equals(outcome)) {
                httpMessageContext.getMessageInfo().getMap().put("javax.servlet.http.registerSession", TRUE.toString());
            }
            
            return outcome;
        }
        
        return invocationContext.proceed();
    }
    
    private static boolean isImplementationOf(Method implementationMethod, Method interfaceMethod) {
        return
            interfaceMethod.getDeclaringClass().isAssignableFrom(implementationMethod.getDeclaringClass()) &&
            interfaceMethod.getName().equals(implementationMethod.getName()) &&
            Arrays.equals(interfaceMethod.getParameterTypes(), implementationMethod.getParameterTypes());
    }
    
    private static Method getMethod(Class<?> base, String name, Class<?>... parameterTypes) {
        try {
            // Method literals in Java would be nice
            return base.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}