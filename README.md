OAuth2 demonstration
====================

Please see http://wiki.nikhef.nl/grid/CLARIN/OAuth2_use_case for more details.

This is a demonstration of a complete OAuth2 setup, with separate client,
authorization server and resource server roles. Client and resource are
Java-servlet-based, the authorization server can be anything. At the moment,
[Spring Security] plus [Spring Security OAuth] are used at the client and
resource side, while [ndg_oauth_server] is used as the authorization server.


Scenario
--------

A user visits a website (client), showing a list of food residing at a remote
service.  When the user is logged into the remote service, additional food is
available that can only be obtained when being logged in.

Three services are running simultaneously, and interacting:

* __Client__ -
  website that the user visits. Obtains a list of food from the service.

* __Authorization Server (AS)__ -
  where checks for authorization are executed.

* __Service__ -
  web service that returns a food list. This is list different for
  authenticated and anonymous users. Relies on the authorization server for
  verifying authenticated requests (bearer tokens).


Getting started
---------------

The demonstration is setup to run all the services on localhost, so that it
works out of the box. Requirements: [Maven], [Python] with [setuptools]
and [pyOpenSSL]. Debian packages: `maven python-setuptools python-openssl`.

Each of these services is kept in a separate branch of this project. So
checkout the three branches and run the service contained in each. 

```shell
# in one terminal
git clone -b service-master https://github.com/wvengen/oauth2-demo service-master
cd service-master
mvn tomcat:run
# in another terminal
git clone -b as-ndg-master https://github.com/wvengen/oauth2-demo as-ndg-master
easy_install [--user] https://github.com/downloads/wvengen/ndg_oauth_server/ndg_oauth_server-0.4.0.tar.gz
cd as-ndg-master
PATH=$PATH:$HOME/.local/bin # if paster was installed as user
paster serve bearer_tok_server_app.ini
# in yet another terminal
git clone -b client-master https://github.com/wvengen/oauth2-demo client-master
cd client-master
mvn tomcat:run
```

The service runs by default on port 8080, the authorization server on port 8082
and the client on port 8081. Visit the client at http://localhost:8081/ and
when asked for a username/password supply `user`/`user` (once to log into the
client and once at the authorization server).
Clear cookies to logout from the AS.


Run services on a single remote host
------------------------------------

When you want to run these services on a single remote host, the configuration
needs to be adapted a little so that URLs that are exposed to the user can be
found.

1. __Authorization server URL__: the client needs to redirect the user to the
   AS at a publically visible URL. Modify the client's
   `src/main/webapp/WEB-INF/spring-security.xml`:
       <oauth:resource id="foodService" ...
           user-authorization-uri="https://my.example.com:8082/oauth/authorize" ...

2. __Redirect URL__: the authorization server needs to know that it is allowed
   to redirect back to the client at a certain URL. Modify the AS's
   `client_register.ini` and add the client's secure URL:
       redirect_uris=http://localhost:8081/secure,http://my.example.com:8081/secure

Please note that the server certificate for the authorization server will now
appear as invalid to the user, since the certificate supplied with this
demonstration only includes localhost. You can ignore the error for this demo.




[Spring Security]: http://static.springsource.org/spring-security/
[Spring Security OAuth]: https://github.com/SpringSource/spring-security-oauth
[ndg_oauth_server]: https://github.com/wvengen/ndg_oauth_server
[NDG ndg_oauth_server]: http://ndg-security.ceda.ac.uk/browser/trunk/ndg_oauth

[Maven]: http://maven.apache.org/
[Python]: http://www.python.org/
[setuptools]: http://pypi.python.org/pypi/setuptools
[pyOpenSSL]: http://pypi.python.org/pypi/pyOpenSSL
