package javax.security;

import java.security.Principal;

public class CallerPrincipal implements Principal {
    
    private final String name;
    
    public CallerPrincipal(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

}
