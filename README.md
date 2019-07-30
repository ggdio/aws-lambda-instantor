AWS Lambda :: Instantor Callback Function
==============================================

```xml
<dependency>
    <groupId>br.com.ggdio</groupId>
	<artifactId>aws-lambda-instantor</artifactId>
	<version>1.0.0-RELEASE</version>
</dependency>
```

For AWS documentation please go to: https://docs.aws.amazon.com/pt_br/lambda

For Instantor documentation please go to: https://www.instantor.com/api/doc

For usage questions, please use [stack overflow with the “aws-lambda” tag](https://stackoverflow.com/questions/tagged/amazon-web-services+lambda?tab=Newest) 

Getting started
---------------

Install [Instantor LIB](https://www.instantor.com/api/doc#api-download)

```shell
cd lib/
sh install.sh
```

Dependency:

```xml
<dependency>
    <groupId>br.com.ggdio</groupId>
	<artifactId>aws-lambda-instantor</artifactId>
	<version>1.0.0-RELEASE</version>
</dependency>
```

Lambda Expression:

```text
br.com.ggdio.InstantorLambda::handleRequest
```


Build: 

    mvn clean package