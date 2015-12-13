# mechanism-to-store-x
Extended version of "mechanism-to-store"; working application that demonstrates how an auth module can delegate to a JSR 375 identity store

JSR 375 API and implementation resides within the jsr375 module.

Example applications reside within the app-`X` modules, where `X` represents a specific identity store type:

* **app-mem** - Uses the embedded in-memory identity store. The application sets the data to be used by means of an annotation
* **app-db**  - Uses the database identity store. The applications defines an embedded datasource and binds this to the identity store definition via an annotation. The data to be used is inserted in the datasoource by the application during startup.
* **app-ldap** - Uses the LDAP identity store. The application instantiates an embedded LDAP server and binds its URL to the identity store definition via an annotation. The data to be used is inserted in the LDAP server by the application during startup.
* **app-custom** - Uses an identity store that's full provided by the application. Just for the example, this store does the caller name/credential check internally.

Each application uses a test SAM (authentication module) that's also provided by the application. This SAM takes the caller name and password directly from the request. **NOTE**: This is for demonstration purposes only and is obviously not a very good practice for real scenarios.

After deploying an application to a server that's listening to localhost:8080, an example of how it can be invoked is as follows:

http://localhost:8080/app-ldap/servlet?name=reza&password=secret1

This should print:

    This is a servlet 
    web username: reza
    web user has role "foo": true
    web user has role "bar": true
    web user has role "kaz": false

    