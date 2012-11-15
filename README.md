Example OAuth 2.0 bearer token authorisation and resource server app
====================================================================

  
To Run
======

* Make sure you have installed the `ndg_oauth_server` Python package:

        easy_install [--user] https://github.com/downloads/wvengen/ndg_oauth_server/ndg_oauth_server-0.4.0.tar.gz

  This is a customised version that includes support for password-based
  client authentication and resource authentication. Work is underway
  to get this integrated upstream.

  Otherwise, this should work with version 0.3.1 and 0.4.0 of the
  official `ndg_oauth_server` package available from [PyPi].

* Run this application.

        PATH=$PATH:$HOME/.local/bin # if paster was installed as user
        paster serve bearer_tok_server_app.ini

  The standard configuration listens on port 8082 on localhost. You can
  login with user `user` and password `user`. Clear out cookies to reset
  between test runs.

[PyPi]: http://pypi.python.org/
