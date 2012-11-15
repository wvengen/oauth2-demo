OAuth2 demonstration
====================

Please see http://wiki.nikhef.nl/grid/CLARIN/OAuth2_use_case for more details.

This is a demonstration of a complete OAuth2 setup, with separate client,
authorization server and resource server roles. Client and resource are
Java-servlet-based, the authorization server can be anything. At the moment,
[Spring Security] plus [Spring Security OAuth] are used at the client and
resource side, while [ndg_oauth_server] is used as the authorization server.

View code for the
[service](https://github.com/wvengen/oauth2-demo/tree/service-master),
[authorization server](https://github.com/wvengen/oauth2-demo/tree/as-ndg-master),
[client](https://github.com/wvengen/oauth2-demo/tree/client-master).


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
   `src/main/webapp/WEB-INF/spring-servlet.xml`:

        <oauth:resource id="foodService" ...
            user-authorization-uri="https://my.example.com:8082/oauth/authorize" ...

2. __Redirect URL__: the authorization server needs to know that it is allowed
   to redirect back to the client at a certain URL. Modify the AS's
   `client_register.ini` and add the client's secure URL:

        redirect_uris=http://localhost:8081/secure,http://my.example.com:8081/secure

Please note that the server certificate for the authorization server will now
appear as invalid to the user, since the certificate supplied with this
demonstration only includes localhost. You can ignore the error for this demo.


Running behind Apache
---------------------

For a production deployments you'll probably want to run services using a
webserver like [Apache].
How the authorization server can run from Apache is explained in ndg_oauth_server's
[README](http://ndg-security.ceda.ac.uk/browser/trunk/ndg_oauth/README.txt#L266),
but the following would work as well:

1. Enable [modwsgi] for Apache. Debian package: `libapache2-mod-wsgi`.

2. Add the OAuth2 AS to the apache configuration. On Debian, you could create
   the file `/etc/apache2/sites-available/oauth2-as` and run `a2ensite oauth2-as`.

        <Directory "/path/to/as-ndg-master/">
          SSLRequireSSL on

          SSLVerifyClient optional_no_ca
          SSLVerifyDepth  10
          SSLOptions +StdEnvVars +ExportCertData

          # Pass the Authorization header to the WSGI middleware/application.
          WSGIPassAuthorization On

          Order allow,deny
          Allow from all
        <Directory>

        WSGIDaemonProcess oauth2-server processes=2 threads=15 display-name=%{GROUP} user=user group=user
        WSGIProcessGroup oauth2-server

        # here the OAuth2 AS is served at /, which means that the authorization
        #   endpoint can be found at /oauth/authorize (with supplied .ini file)
        WSGIScriptAlias / /path/to/as-ndg-master/serve.wsgi

3. Make sure SSL is enabled, and you have a key and certificate for your host.
   On Debian, you may get started by running `a2ensite default-ssl`.

4. Make sure the certificate authority used for the AS is trusted by both
   clients and resources. For the demonstration, you can use the supplied
   `host.pem` for `SSLCertificateFile` and `SSLCertificateKeyFile`, and
   `host_ca.pem` for `SSLCertificateChainFile`.

You may want to run Apache on port 8082 with SSL enabled, so that the
demonstration works without other configuration changes. On Debian, this is
done by changing `/etc/apache2/ports.conf` to `Listen 8082`, and
`/etc/apache2/sites-enabled/default-ssl`'s virtual host definition to
`<VirtualHost _default_:8082>`.


Using single sign-on at the authorization server
------------------------------------------------

A smooth user experience can be had when single sign-on is used on both the
client and the authorization server. How to setup single sign-on at the client
is outside the scope of this demonstration (it is similar to enabling it for
the AS).

Assuming that the AS is run using Apache, as described above, the [Shibboleth]
module can be used to enable SAML single sign-on. This is
[covered](https://wiki.shibboleth.net/confluence/display/SHIB2/NativeSPApacheConfig) at
[many](http://www.edugate.ie/Support/Technical%20Resources/Installation%20Guides/Service%20Provider%20Guides/shibboleth-2-service-provi-0)
[other](https://itservices.stanford.edu/service/shibboleth/sp)
[places](http://www.switch.ch/aai/support/serviceproviders/sp-access-rules.html).
To get started, a very short overview of connecting to Feide's [OpenIdP] is
given here.

1. Install the Shibboleth daemon and the Apache module.
   Debian package: `libapache2-mod-shib2`, then run `a2enmod shib2`.
   You may want to run `shib-keygen` as well to generate a default certificate.

2. Configure the Shibboleth daemon in `/etc/shibboleth/shibboleth2.xml`.
   This includes the sections `ApplicationDefaults` and `Sessions` and
   `MetadataProvider`. For OpenIdP, the following adaptation would suffice:

        <ApplicationDefaults ...
             entityID="https://my.example.com:8082/oauth" ...>

          <Sessions ...>
             <SessionInitiator type="Chaining" Location="/Login" isDefault="true" id="Intranet"
                     relayState="cookie" entityID="https://openidp.feide.no">
               ...
             </SessionInitiator>
             ...
          </Sessions>

          <MetadataProvider>
            <MetadataProvider type="XML" file="openidp-metadata.xml"/>
          </MetadataProvider>

        </ApplicationDefaults>

   Then download the [OpenIdP metadata](https://openidp.feide.no/simplesaml/saml2/idp/metadata.php)
   into the file `openidp-metadata.xml`.

   For Feide's OpenIdP, you need to allow unscoped attributes. Add to
   `/etc/shibboleth/attribute-policy.xml`:

        <afp:AttributeFilterPolicy>
          <afp:AttributeRule attributeID="eppn" />
        </afp:AttributeFilterPolicy>

   Then restart the Shibboleth daemon: `/etc/init.d/shibd restart`.

   You probably need to register your service. This is done by pasting the
   output of your SAML metadata, found at
   http://my.example.com:8082/Shibboleth.sso/Metadata , into the 
   [OpenIdP SAML registration](https://openidp.feide.no/simplesaml/module.php/metaedit/xmlimport.php)
   page and completing the registration procedure.

3. Configure Apache to require authentication at the AS:

        # Require Shibboleth authentication for the authorize endpoint
        <Location /client_authorization>
          AuthType Shibboleth
          Require shibboleth
          ShibRequireSession On
        </Location>
        <Location /oauth/authorize>
          AuthType Shibboleth
          Require shibboleth
          ShibRequireSession On
        </Location>

        # to keep Shibboleth URLs working with WSGIScriptAlias in the root
        <Location /Shibboleth.sso>
          SetHandler shib
        </Location>
        Alias /shibboleth-sp /usr/share/shibboleth

   Finally restart Apache.


Moving to production
--------------------

This section can only be finalised when the move has been made. For now, it
entails at least:

* Turning off debugging options on the authorization server
  - In `[filter:AuthenticationFormFilter]`, enable `set debug = false`
  - Only log `WARNING`s or `INFO` messages by changing their `level`

* Make sure any test accounts in `passwd` are removed.
  - potentially disable local accounts completely in `repoze_who.ini`.

* The example Shibboleth configuration here is probably not suitable for
  your production situation. Make sure it is sound and safe.

* Check permissions on files and directories used.


[Spring Security]: http://static.springsource.org/spring-security/
[Spring Security OAuth]: https://github.com/SpringSource/spring-security-oauth
[ndg_oauth_server]: https://github.com/wvengen/ndg_oauth_server
[NDG ndg_oauth_server]: http://ndg-security.ceda.ac.uk/browser/trunk/ndg_oauth
[Maven]: http://maven.apache.org/
[Python]: http://www.python.org/
[setuptools]: http://pypi.python.org/pypi/setuptools
[pyOpenSSL]: http://pypi.python.org/pypi/pyOpenSSL
[Apache]: http://httpd.apache.org/
[Shibboleth]: http://shibboleth.net/
[modwsgi]: https://code.google.com/p/modwsgi/
[OpenIdP]: https://openidp.feide.no/
