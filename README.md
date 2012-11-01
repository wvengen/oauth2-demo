OAuth2 demonstration
====================

Please see http://wiki.nikhef.nl/grid/CLARIN/OAuth2_use_case for more details.
(**todo:** describe project)

This is a demonstration of a complete OAuth2 setup, with separate client,
authorization server and resource server roles. Client and resource are
Java-servlet-based, the authorization server can be anything.


*Please note that this is quite a work in progress.*

*DOES NOT work currently without disabling the https requirement by patching
ndg\_oauth\_server's source code. Sorry!*


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


Each of these services is kept in a separate branch of this project. So checkout
the three branches and run the service contained in each.

```shell
git clone https://github.com/wvengen/oauth2-demo
# in one terminal
git clone -b service-master oauth2-demo service-master
cd service-master
mvn tomcat:run
# in another terminal
git clone -b as-ndg-master oauth2-demo as-ndg-master
easy_install [--user] ndg_oauth_server
cd as-ndg-master
python bearer_tok_server_app_serve.py
# in again another terminal
git clone -b client-master oauth2-demo client-master
cd client-master
mvn tomcat:run
```

The service runs by default on http://localhost:8080/ ,
the authorization server on http://localhost:8082/
and the client on http://localhost:8081/ . Visit the client, and when asked for
a username/password supply `user`/`user`. Clear cookies to clear login.

