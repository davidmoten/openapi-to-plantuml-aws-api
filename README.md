# openapi-to-plantuml-aws-api

In development

Design
* create artifact bucket if does not exist
* deploy artifact to artifact bucket
* create public bucket if does not exist
* deploy static resources to public bucket
* deploy apig and lambda via cloudformation that references artifact object and public bucket in apig methods
* generate cert for new domain in AWS Certificate Manager
* set new domain as alias for apig hostname
* associate new cert with apig

## Notes
Assumption: you have a Route 53 domain name (mine is davidmoten.org)

* Create certificate in AWS Certificate Manager (your own region is fine, ignore old advice that you need to use us-east-1), *don't* expand certificate name during wizard to request Route 53 update CNAME records because the routing won't use CNAME.
* make sure user deploying CloudFormation has UpdateDistribution on '*' permission (or Full Access)
* make sure user deploying CloudFormation has Certificate Manager access (Full will work obviously but you can probably narrow that)
* Go to Route 53 and add new domain name (as per AWS Certificate Manager) of type A, Alias = Yes, select API Gateway from the dropdown and then select the API Gateway domain name (which is also visible in API Gateway - Custom Domain Names).

