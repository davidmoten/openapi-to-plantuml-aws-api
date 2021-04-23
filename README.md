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

* In AWS Certificate Manager change to us-east-1 region (essential for apig and certs to work)
* Create certificate, expand certificate name during wizard and request Route 53 update CNAME records
* make sure user deploying CloudFormation has UpdateDistribution on '*' permission (or Full Access)
* make sure user deploying CloudFormation has Certificate Manager access (Full will work obviously but you can probably narrow that)

