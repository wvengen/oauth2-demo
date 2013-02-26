OAuth 2.0 based short-lived credential demonstration
====================================================

Please see http://wiki.nikhef.nl/grid/CLARIN/OAuth2_use_case for more details.

This is a demonstration of a complete setup where a client retrieves a
short-lived certificate, and accesses a protected service. Retrievel of
the certificate is done using OAuth 2.0.

Client and resource are Java-servlet-based, the authorization server is
provided by [ndg_oauth_server]; [Spring Security] plus [Spring Security OAuth]
are used at the client and resource side.

View code for the
[service](https://github.com/wvengen/oauth2-demo/tree/eudat-service-master),
[authorization server](https://github.com/wvengen/oauth2-demo/tree/eudat-as-master),
[client](https://github.com/wvengen/oauth2-demo/tree/eudat-client-master).


Scenario
--------

A user visits a website (client), showing a list of food residing at a remote
service.  When the user is logged into the remote service, additional food is
available that can only be obtained when being logged in.

Three services are running simultaneously, and interacting:

* __Client__ -
  website that the user visits. Obtains a list of food from the service.

* __Authorization Server (AS)__ -
  where authentication is done and security tokens are handed out.

* __Service__ -
  web service that returns a food list. This is list different for
  authenticated and anonymous users. Relies on client certificates
  for verifying authenticated requests.


Getting started
---------------

The demonstration is setup to run all the services on localhost, so that it
works out of the box. Requirements: [Maven], [Python] with [setuptools]
and [pyOpenSSL]. Debian packages: `maven python-setuptools python-openssl`.

Each of these services is kept in a separate branch of this project. So
checkout the three branches and run the service contained in each. 

```shell
# in one terminal
git clone -b eudat-service-master https://github.com/wvengen/oauth2-demo eudat-service-master
cd eudat-service-master
mvn jetty:run
# in another terminal
git clone -b eudat-as-master https://github.com/wvengen/oauth2-demo eudat-as-master
easy_install [--user] ndg_oauth_server
easy_install [--user] ContrailOnlineCAService
cd eudat-as-master
PATH=$PATH:$HOME/.local/bin # if paster was installed as user
paster serve slcs_server_app.ini
# in yet another terminal
git clone -b eudat-client-master https://github.com/wvengen/oauth2-demo eudat-client-master
cd eudat-client-master
mvn tomcat:run
```

The service runs by default on port 8080, the authorization server on port 8082
and the client on port 8081. Visit the client at http://localhost:8081/ and
when asked for a username/password supply `user`/`user` (once to log into the
client and once at the authorization server).
Clear cookies to logout from the AS.


[Spring Security]: http://static.springsource.org/spring-security/
[Spring Security OAuth]: https://github.com/SpringSource/spring-security-oauth
[ndg_oauth_server]: https://github.com/wvengen/ndg_oauth/tree/master/ndg_oauth_server
[NDG ndg_oauth_server]: http://ndg-security.ceda.ac.uk/browser/trunk/ndg_oauth
[Maven]: http://maven.apache.org/
[Python]: http://www.python.org/
[setuptools]: http://pypi.python.org/pypi/setuptools
[pyOpenSSL]: http://pypi.python.org/pypi/pyOpenSSL
[Apache]: http://httpd.apache.org/
[Shibboleth]: http://shibboleth.net/
[modwsgi]: https://code.google.com/p/modwsgi/
[OpenIdP]: https://openidp.feide.no/
