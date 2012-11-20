OAuth 2.0 and Resource Endpoints
================================

Terminology:
* __Client__: program that accesses an OAuth-protected resource. This
      includes web applications, portals and command-line clients.
* __Resource__: service that a client wants to access.
* __Authorization Server (AS)__: web service that provides and checks
      OAuth 2.0 security tokens.

When using other OAuth Clients, it may be useful to know about the RESTful
endpoints involved. There are three endpoints involved: authorize,
access\_token check\_token. The first two are defined by the [OAuth 2.0 RFC],
the latter is not.

The OAuth flow currently implemented by `ndg_oauth_server` is the
[authorization code flow], meant for web applications/portals. All endpoint
urls specified are defaults at the authorization server; they can be changed in
the authorization server's configuration file.


Endpoint: authorization
-----------------------

The [authorization endpoint] is used to obtain an authorization code. The
user's webbrowser is redirected to the authorization endpoint, where
authentication is done and user consent is obtained. Then the user's webbrowser
is redirected back to the client.

* ___endpoint url___: `/oauth/authorize`
* ___parameters___:
   `response_type` `client_id` `redirect_uri` `scope` `state`
   as described in [OAuth2/4.1.1](http://tools.ietf.org/html/rfc6749#section-4.1.1).
* ___authentication___: not applicable, since it is a redirect
* ___response___: http get parameters in redirect: `code` `state`
   as described in [OAuth2/4.1.2](http://tools.ietf.org/html/rfc6749#section-4.1.2).
   

Endpoint: access\_token
-----------------------

The [access\_token endpoint] is used to obtain an access code at server-side
web application, after obtaining a code from the authorization server.

* ___endpoint url___: `/oauth/access_token`
* ___parameters___:
   `grant_type` `code` `redirect_uri` `client_id`
   as described in [OAuth2/4.1.3](http://tools.ietf.org/html/rfc6749#section-4.1.3).
* ___authentication___: client secret (default) or client certificate
   as defined in the `ndg_oauth_server` configuration file and `client_register.ini`.
* ___response___: json structure with access token
   as described in [OAuth2/4.1.4](http://tools.ietf.org/html/rfc6749#section-4.1.4).


Endpoint: check\_token
----------------------

When the client accesses the resource providing an access token, the resource
needs to check that the token is valid. This is done by contacting the
authorization server's check\_token endpoint. A user identity is returned.

This endpoint is not specified in the OAuth 2.0 RFC. There are other
implementations, however, and it would be useful to to keep them interoperable
where possible, like [CloudFoundry's RemoteTokenServices].


* ___endpoint url___: `/oauth/check_token`
* ___parameters___:
  - `access_token`: the access token to check for validity
  - `scope`: scope to require for the access token **(optional)**
* ___authentication___: resource secret (default) or resource certificate
   as defined in the `ndg_oauth_server` configuration file and `resource_register.ini`
   (only in fork mentioned in README.md).
* ___response___: json structure with at least a `user_name` field.


[OAuth 2.0 RFC]: http://tools.ietf.org/html/rfc6749
[authorization code flow]: http://tools.ietf.org/html/rfc6749#page-24
[authorization endpoint]: http://tools.ietf.org/html/rfc6749#section-3.1
[access\_token endpoint]: http://tools.ietf.org/html/rfc6749#section-3.2
[CloudFoundry's RemoteTokenServices]: https://github.com/cloudfoundry/uaa/blob/master/common/src/main/java/org/cloudfoundry/identity/uaa/oauth/RemoteTokenServices.java

