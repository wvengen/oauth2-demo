Example OAuth 2.0 bearer token authorisation and resource server app
====================================================================

  
To Run
======

* Make sure you have installed the `ndg_oauth_server` Python package:
      easy_install [--user] ndg_oauth_server
  This has been tested with version 0.3.1 of `ndg_oauth_server`.

* Run this application.
      python bearer_tok_server_app_serve.py
  The standard configuration listens on port 8082 on localhost. You can
  login with user `user` and password `user`. Clear out cookies to reset
  between test runs.

