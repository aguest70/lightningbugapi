# Local Cache #

When the client is connected to a Bugzilla instance (identified by the URL) it will store a local copy of the product configuration (including components, severity, versions, target milestones and so on) of the instance in a XML file, called the local cache.

The next time the client is connected to the same instance the information in the local cache are being use, when the client returns the product config.

A thread in the background will update the local config soon after the client connects. When the update thread is finished the client will return the current product config.

Because the product configuration of a Bugzilla instance doesn't change very often, this approach will save a lot of time at start up.

The XML file containing the local cache is saved in the home folder of the current user (depending on the value of the Java system property `user.home`).