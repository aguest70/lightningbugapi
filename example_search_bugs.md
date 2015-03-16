
```
//connect to bugzilla
final BugzillaClient client = new BugzillaClient(new URL("http://host/bugzilla-4.0.1/"), "email@host.de","password");

//create the bug service
final BugService bugService = new BugService(client);

//define the search parameters
final Map<String, Object[]> searchParams = new HashMap<String, Object[]>();
searchParams.put("target_milestone", new Object[]{ "0.4.2", "0.4.1" });
searchParams.put("priority", new Object[]{ "Normal"});

//search for matching bugs
final List<Bug> bugs = bugService.search(searchParams);

//diplay the number of bugs of version 0.0.1 and 0.0.2 with the priority 'Normal'
System.out.println(bugs.size());
```