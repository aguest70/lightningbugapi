# About #

LightingBugAPI is a Java API to access Bugzilla (+3.6) via XMLRPC. It is not intended to be complete, since the XML-RPC-Interface of Bugzilla is constantly being enhanced. But we try to cover the basics here. So this might be a good starting point to integrate Bugzilla into your Java application.

# New version has been released! #

The 2nd version (current version  4.0.1v20120822 - version number according to the supported bugzilla version plus the build date) of the API has been released. And we have a new logo too :)

# What's new? #

  * The API as been redesigned. The `BugzillaClient` is no longer used to query product(s) an bugs. These tasks are now performed by the `ProductService` and the `BugService` according to  the [Bugzilla Web Service](http://www.bugzilla.org/docs/4.0/en/html/api/Bugzilla/WebService.html).
  * A feature called [Local Cache](local_cache.md) will store the products configuration of a Bugzilla instance on the client. The second attempt to connect the client and query product information will use the locally stored information and significantly speed up the query
  * The creation of bugs has been temporarily disabled an will be supported again in the next version
  * The domain objects `Version`, `TargetMilestone`, `Component` and  `Severity` have been removed. These information are now plain strings (see `Product.java`)

# Setup #

  1. First of all download the binary distribution from the download section
  1. Extract the content of the archive (the archive contains the jar with the binary files and a `lib` folder with the required third party libraries)
  1. Place the LightningBug-API-jar and the required libraries into your classpath
  1. Follow the samples below to connect to Bugzilla and query product information or search for bugs

# Code samples #

  1. [Log in to Bugzilla](example_connect.md)
  1. [Query product(s) configuration](example_get_products.md)
  1. [Search for bugs](example_search_bugs.md)
  1. [Get a list of users](example_get_users.md)

# Links to other interesting resources #

  * http://code.google.com/p/j2bugzilla/