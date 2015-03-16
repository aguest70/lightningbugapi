
```
//connect to bugzilla
final BugzillaClient client = new BugzillaClient(new URL("http://host/bugzilla-4.0.1/"), "email@host.de","password");

//create the user service
final UserService service = new UserService(client);

//query the users
final Set<User> activeUsers = service.getActiveUsers();

//print the result
for(final User user : activeUsers){
   System.out.println(user);
}
```