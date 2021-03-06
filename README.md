# mechanism-to-store-x
Extended version of "mechanism-to-store"; working application that demonstrates how an auth module can delegate to a JSR 375 identity store

JSR 375 API and implementation resides within the jsr375 module.

Example applications reside within the app-`X` modules, where `X` represents a specific identity store type:

* **app-mem** - Uses the embedded in-memory identity store. The application sets the data to be used by means of an annotation
* **app-db**  - Uses the database identity store. The applications defines an embedded datasource and binds this to the identity store definition via an annotation. The data to be used is inserted in the datasoource by the application during startup.
* **app-ldap** - Uses the LDAP identity store. The application instantiates an embedded LDAP server and binds its URL to the identity store definition via an annotation. The data to be used is inserted in the LDAP server by the application during startup.
* **app-custom** - Uses an identity store that's full provided by the application. Just for the example, this store does the caller name/credential check internally.
* **app-mem-basic** - As app-mem but uses the JSR 375 provided BASIC authentication mechanism
* **app-custom-session** - As app-custom, but uses a JSR 375 provided interceptor to automatically establish an authentication session when authenticated. 
  * Check initially not authenticated: http://localhost:8080/app-custom-session/servlet
  * authenticate: http://localhost:8080/app-custom-session/servlet?name=reza&password=secret1
  * Check authentication remembered: http://localhost:8080/app-custom-session/servlet
  * logout: http://localhost:8080/app-custom-session/servlet?logout
* **app-custom-session** - As app-session, but uses a JSR 375 provided interceptor to conditionally remember the caller by writing a cookie and storing the details in an appplication provided special purpose identity store
  * Check initially not authenticated: http://localhost:8080/app-custom-session/servlet
  * authenticate: http://localhost:8080/app-custom-session/servlet?name=reza&password=secret1
  * Check authentication NOT remembered: http://localhost:8080/app-custom-session/servlet
  * authenticate with remember me: http://localhost:8080/app-custom-session/servlet?name=reza&password=secret1&rememberme=true
  * Check authentication remembered: http://localhost:8080/app-custom-session/servlet
  * logout: http://localhost:8080/app-custom-session/servlet?logout

Each application uses a test SAM (authentication module) that's also provided by the application. This SAM takes the caller name and password directly from the request. **NOTE**: This is for demonstration purposes only and is obviously not a very good practice for real scenarios.

After deploying an application to a server that's listening to localhost:8080, an example of how it can be invoked is as follows:

http://localhost:8080/app-ldap/servlet?name=reza&password=secret1

This should print:

    This is a servlet 
    web username: reza
    web user has role "foo": true
    web user has role "bar": true
    web user has role "kaz": false

    