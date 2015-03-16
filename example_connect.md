The strings `email` and `password` and the URL to the bugzilla instance, have to be replaced by valid values from your bugzilla installation.

```
 public static void main(String[] args) {
    try {
       final BugzillaClient bugzillaClient = new BugzillaClient(new URL(
          "http://host:port/bugzilla-4.0.1/"), "email", "password");
       bugzillaClient.login();
    } catch (MalformedURLException e) {
       e.printStackTrace();
    }
 }
```