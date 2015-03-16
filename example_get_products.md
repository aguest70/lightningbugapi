
```
//connect to bugzilla
final BugzillaClient client = new BugzillaClient(new URL("http://host/bugzilla-4.0.1/"), "email@host.de","password");

//create the product service an query all products
final List<Product> products = new ProductService(client).getProducts();

//display the product configuration
for(final Product product : products){

   System.out.print(product.getId() + ": ");
   System.out.println(product.getName());

   System.out.println("Components:");
   final Set<String> components = product.getComponents();
   for(String component : components){
      System.out.println(component);
   }

   System.out.println("Severities:");
   final Set<String> severities = product.getSeverities();
   for(final String severity : severities){
      System.out.println(severity);
   }

   System.out.println("Versions:");
   final Set<String> versions = product.getVersions();
   for(final String version : versions){
      System.out.println(version);
   }

   System.out.println("Milestones:");
   final Set<String> milestones = product.getMilestones();
   for(final String milestone : milestones){
      System.out.println(milestone);
   }
}
```