import httplib
import urllib

headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
conn = httplib.HTTPConnection("alert-height-91305.appspot.com")
params = {'Mode' : 'Login', 'id' : '1st'}
params = urllib.urlencode(params)
conn.request("POST", "/hello", params, headers)
response = conn.getresponse()
data = response.read()
print(response.status, response.reason)
print(len(data))
print(data)
conn.close()
