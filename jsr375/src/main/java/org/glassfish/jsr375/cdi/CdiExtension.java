package org.glassfish.jsr375.cdi;

import static org.glassfish.jsr375.cdi.CdiUtils.getAnnotation;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.security.authenticationmechanism.http.HttpAuthenticationMechanism;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.annotation.DataBaseIdentityStoreDefinition;
import javax.security.identitystore.annotation.EmbeddedIdentityStoreDefinition;
import javax.security.identitystore.annotation.LdapIdentityStoreDefinition;

import org.glassfish.jsr375.identitystores.DataBaseIdentityStore;
import org.glassfish.jsr375.identitystores.EmbeddedIdentityStore;
import org.glassfish.jsr375.identitystores.LDapIdentityStore;

public class CdiExtension implements Extension {

    // Note: for now use the highlander rule: "there can be only one" for
    // both identity stores and (http) authentication mechanisms.
    // This could be extended later to support multiple
    private Bean<IdentityStore> identityStoreBean;
    private boolean httpAuthenticationMechanismFound;

    public <T> void processBean(@Observes ProcessBean<T> eventIn, BeanManager beanManager) {

        ProcessBean<T> event = eventIn; // JDK8 u60 workaround

        // TODO: 
        // * What if multiple definitions present?
        // *   -> Make created Bean<T>s alternatives
        // *   -> Throw exception?
        
        Optional<EmbeddedIdentityStoreDefinition> optionalEmbeddedStore = getAnnotation(beanManager, event.getAnnotated(), EmbeddedIdentityStoreDefinition.class);
        if (optionalEmbeddedStore.isPresent()) {
            identityStoreBean = new CdiProducer<IdentityStore>()
                .scope(ApplicationScoped.class)
                .types(IdentityStore.class)
                .create(e -> new EmbeddedIdentityStore(optionalEmbeddedStore.get().value()));
        }
        
        Optional<DataBaseIdentityStoreDefinition> optionalDBStore = getAnnotation(beanManager, event.getAnnotated(), DataBaseIdentityStoreDefinition.class);
        if (optionalDBStore.isPresent()) {
            identityStoreBean = new CdiProducer<IdentityStore>()
                .scope(ApplicationScoped.class)
                .types(IdentityStore.class)
                .create(e -> new DataBaseIdentityStore(optionalDBStore.get()));
        }
        
        Optional<LdapIdentityStoreDefinition> optionalLdapStore = getAnnotation(beanManager, event.getAnnotated(), LdapIdentityStoreDefinition.class);
        if (optionalLdapStore.isPresent()) {
            identityStoreBean = new CdiProducer<IdentityStore>()
                .scope(ApplicationScoped.class)
                .types(IdentityStore.class)
                .create(e -> new LDapIdentityStore(optionalLdapStore.get()));
        }
        
        if (event.getAnnotated().getTypeClosure().contains(HttpAuthenticationMechanism.class)) {
            // enabled bean implementing the HttpAuthenticationMechanism found
            httpAuthenticationMechanismFound = true;
        }
        
    }

    public void afterBean(final @Observes AfterBeanDiscovery afterBeanDiscovery) {
        if (identityStoreBean != null) {
            afterBeanDiscovery.addBean(identityStoreBean);
        }
    }
    
    public boolean isHttpAuthenticationMechanismFound() {
        return httpAuthenticationMechanismFound;
    }

}
