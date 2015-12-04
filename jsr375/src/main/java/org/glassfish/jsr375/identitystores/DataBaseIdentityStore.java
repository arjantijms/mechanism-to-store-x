package org.glassfish.jsr375.identitystores;

import static javax.security.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import static org.glassfish.jsr375.cdi.CdiUtils.jndiLookup;

import javax.security.identitystore.CredentialValidationResult;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.annotation.DataBaseIdentityStoreDefinition;
import javax.security.identitystore.credential.Credential;
import javax.security.identitystore.credential.UsernamePasswordCredential;
import javax.sql.DataSource;

public class DataBaseIdentityStore implements IdentityStore {

    private DataBaseIdentityStoreDefinition dataBaseIdentityStoreDefinition;

    public DataBaseIdentityStore(DataBaseIdentityStoreDefinition dataBaseIdentityStoreDefinition) {
        this.dataBaseIdentityStoreDefinition = dataBaseIdentityStoreDefinition;
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            return validate((UsernamePasswordCredential) credential);
        }

        return NOT_VALIDATED_RESULT;
    }

    public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {

        DataSource dataSource = jndiLookup(dataBaseIdentityStoreDefinition.dataSourceLookup());
        
        return null;
    }

}
