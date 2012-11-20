#!/usr/bin/env python
import os, sys

#sys.path.append('/these/are/my/extra/sources')
#os.environ['PYTHON_EGG_CACHE'] = '/my/custom/egg/cache'

from paste.deploy import loadapp
application = loadapp('config:slcs_server_app.ini', relative_to=os.path.dirname(__file__))
