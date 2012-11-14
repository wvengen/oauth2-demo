OAuth2 demonstration
====================

Please see http://wiki.nikhef.nl/grid/CLARIN/OAuth2_use_case for more details.
(**todo:** describe project)

This is a demonstration of a complete OAuth2 setup, with separate client,
authorization server and resource server roles. Client and resource are
Java-servlet-based, the authorization server can be anything.

*Please note that this is quite a work in progress.*


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


Each of these services is kept in a separate branch of this project. So
checkout the three branches and run the service contained in each. Currently a
patched version of ndg_oauth_server is required, which is available at
[github](https://github.com/wvengen/ndg_oauth_server) (covered in sequence
below).

```shell
# in one terminal
git clone -b service-master https://github.com/wvengen/oauth2-demo service-master
cd service-master
mvn tomcat:run
# in another terminal
git clone -b as-ndg-master https://github.com/wvengen/oauth2-demo as-ndg-master
easy_install [--user] https://github.com/downloads/wvengen/ndg_oauth_server/ndg_oauth_server-0.4.0-py2.7.egg
 # make sure Python Paste is installed, with the paster command
cd as-ndg-master
paster serve bearer_tok_server_app.ini
# in again another terminal
git clone -b client-master https://github.com/wvengen/oauth2-demo client-master
cd client-master
mvn tomcat:run
```

The service runs by default on http://localhost:8080/ ,
the authorization server on http://localhost:8082/
and the client on http://localhost:8081/ . Visit the client, and when asked for
a username/password supply `user`/`user`. Clear cookies to clear login.
