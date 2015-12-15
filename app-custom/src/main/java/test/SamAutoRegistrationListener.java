package test;

import static javax.security.authenticationmechanism.JaspicUtils.registerSAM;

import javax.security.authenticationmechanism.http.HttpBridgeServerAuthModule;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SamAutoRegistrationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        registerSAM(sce.getServletContext(), new HttpBridgeServerAuthModule());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}