package test;

import static javax.security.authenticationmechanism.JaspicUtils.registerSAM;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SamAutoRegistrationListener implements ServletContextListener {
    
    @Inject
    private DatabaseSetup databaseSetup;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        registerSAM(sce.getServletContext(), new TestServerAuthModule());
        databaseSetup.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        databaseSetup.destroy();
        
    }

}