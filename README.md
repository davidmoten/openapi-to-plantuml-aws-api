# openapi-to-plantuml-aws-api
<a href="https://github.com/davidmoten/openapi-to-plantuml-aws-api/actions/workflows/ci.yml"><img src="https://github.com/davidmoten/openapi-to-plantuml-aws-api/actions/workflows/ci.yml/badge.svg"/></a><br/>

This is the source for https://openapi-to-puml.davidmoten.org/prod/site/index.html which converts OpenAPI definitions to PlantUML online. The site uses AWS API Gateway with Lambda and S3 integrations.

This project is a very useful resource if you want to have a custom domain name with a valid certificate pointing to an Api Gateway that uses java Lambda and also integrates with an S3 public bucket.

## Deployment

### Prepare

* Create certificate in AWS Certificate Manager (your own region is fine, ignore old advice that you need to use us-east-1), *don't* expand certificate name during wizard to request Route 53 update CNAME records because the routing won't use CNAME.
* make sure AWS deploying user deploying CloudFormation has UpdateDistribution on '*' permission (or Full Access)
* make sure AWS deploying user deploying CloudFormation has Certificate Manager access (Full will work obviously but you can probably narrow that)

### Deploy
To deploy you need your AWS credential pair (encrypted preferrably but up to you) in `.m2/settings.xml` with the name `my.aws`. If you want to use a different serverId then run the command below with the extra argument `-Dserver.id=<YOUR_SERVER_ID>`.

```bash
./deploy.sh
```
The deploy script does these steps:

* create artifact bucket if does not exist
* deploy artifact to artifact bucket
* create public bucket if does not exist
* deploy static resources to public bucket
* deploy apig and lambda via cloudformation that references artifact object and public bucket in apig methods
* generate cert for new domain in AWS Certificate Manager
* set new domain as alias for apig hostname
* associate new cert with apig
* create Route53 recordset to associate domain name with api gateway
