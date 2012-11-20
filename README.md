OAuth 2.0 based short-lived credential service
==============================================

  
To Run
======

* Make sure you have installed the `ndg_oauth_server` Python package:

        easy_install [--user] ndg_oauth_server

  This uses version 0.4.0, which is available from [PyPi].

* Run this application.

        PATH=$PATH:$HOME/.local/bin # if paster was installed as user
        paster serve slcs_server_app.ini

  The standard configuration listens on port 8082 on localhost. You can
  login with user `user` and password `user`. Clear out cookies to reset
  between test runs.


The standard `ndg_oauth_server` does not support password-based client
authorization (which is used in most other OAuth 2.0 deployments).
The fork at
  https://github.com/downloads/wvengen/ndg_oauth_server/ndg_oauth_server-0.4.0.tar.gz
includes support for password-based client authentication (and resource
authentication). Work is underway to get this integrated upstream.

[PyPi]: http://pypi.python.org/
