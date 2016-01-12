package org.glassfish.jsr375.cdi;

import static java.util.concurrent.TimeUnit.DAYS;
import static javax.interceptor.Interceptor.Priority.PLATFORM_BEFORE;
import static javax.security.auth.message.AuthStatus.SUCCESS;
import static javax.security.identitystore.CredentialValidationResult.Status.VALID;
import static org.glassfish.jsr375.servlet.CookieHandler.getCookie;
import static org.glassfish.jsr375.servlet.CookieHandler.removeCookie;
import static org.glassfish.jsr375.servlet.CookieHandler.saveCookie;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.security.auth.message.AuthStatus;
import javax.security.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.authentication.mechanism.http.HttpMessageContext;
import javax.security.authentication.mechanism.http.annotation.RememberMe;
import javax.security.identitystore.CredentialValidationResult;
import javax.security.identitystore.RememberMeIdentityStore;
import javax.security.identitystore.credential.RememberMeCredential;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Interceptor
@RememberMe
@Priority(PLATFORM_BEFORE + 210)
public class RememberMeInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final static Method validateRequestMethod = getMethod(
        HttpAuthenticationMechanism.class, 
        "validateRequest",
        HttpServletRequest.class, HttpServletResponse.class, HttpMessageContext.class);
    
    private final static Method cleanSubjectMethod = getMethod(
        HttpAuthenticationMechanism.class, 
        "cleanSubject",
        HttpServletRequest.class, HttpServletResponse.class, HttpMessageContext.class);
   
    
    @Inject 
    private Instance<RememberMeIdentityStore> rememberMeIdentityStoreInstance;

    @AroundInvoke
    public Object intercept(InvocationContext invocationContext) throws Exception {
        
        // If intercepting HttpAuthenticationMechanism#validateRequest
        if (isImplementationOf(invocationContext.getMethod(), validateRequestMethod)) {
            return validateRequest(invocationContext,   
                (HttpServletRequest)invocationContext.getParameters()[0],
                (HttpServletResponse)invocationContext.getParameters()[1],
                (HttpMessageContext)invocationContext.getParameters()[2]);
        }
        
        // If intercepting HttpAuthenticationMechanism#cleanSubject
        if (isImplementationOf(invocationContext.getMethod(), cleanSubjectMethod)) {
            cleanSubject(invocationContext,   
                (HttpServletRequest)invocationContext.getParameters()[0],
                (HttpServletResponse)invocationContext.getParameters()[1],
                (HttpMessageContext)invocationContext.getParameters()[2]);
        }
        
        return invocationContext.proceed();
    }
    
    private AuthStatus validateRequest(InvocationContext invocationContext, HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws Exception {
        
        RememberMeIdentityStore rememberMeIdentityStore = rememberMeIdentityStoreInstance.get(); // TODO ADD CHECKS
        
        Cookie rememberMeCookie = getCookie(request, "JREMEMBERMEID");
        
        if (rememberMeCookie != null) {
            
            // There's a JREMEMBERMEID cookie, see if we can use it to authenticate
            
            CredentialValidationResult result = rememberMeIdentityStore.validate(
                new RememberMeCredential(rememberMeCookie.getValue())
            );
            
            if (result.getStatus() == VALID) {
                // The remember me store contained an authenticated identity associated with 
                // the given token, use it to authenticate with the container
                return httpMessageContext.notifyContainerAboutLogin(
                    result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                // The token appears to be no longer valid, or perhaps wasn't valid
                // to begin with. Remove the cookie.
                removeCookie(request, response, "JREMEMBERMEID");
            }
        }
        
        // Try to authenticate with the next interceptor or actual authentication mechanism
        AuthStatus authstatus = (AuthStatus) invocationContext.proceed();
        
        if (authstatus == SUCCESS) {
            
            // Authentication succeeded; store the authenticated identity in the 
            // remember me store and send a cookie with a token that can be used
            // to retrieve this stored identity later
            
            String token = rememberMeIdentityStore.generateLoginToken(
                httpMessageContext.getCallerPrincipal(),
                httpMessageContext.getRoles()
            );
            
            saveCookie(request, response, "JREMEMBERMEID", token, (int) DAYS.toSeconds(14));
        }
        
        return authstatus;
    }
    
    private void cleanSubject(InvocationContext invocationContext, HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
    
        RememberMeIdentityStore rememberMeIdentityStore = rememberMeIdentityStoreInstance.get(); // TODO ADD CHECKS
        
        Cookie rememberMeCookie = getCookie(request, "JREMEMBERMEID");
        
        if (rememberMeCookie != null) {
            
            // There's a JREMEMBERMEID cookie, remove the cookie
            removeCookie(request, response, "JREMEMBERMEID");
            
            // And remove the token (and with it the authenticated identity) from the store
            rememberMeIdentityStore.removeLoginToken(rememberMeCookie.getValue());
        }
        
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