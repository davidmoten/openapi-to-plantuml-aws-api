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