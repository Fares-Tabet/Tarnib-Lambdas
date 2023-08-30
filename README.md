This is boilerplate code for a Java lambda function. To deploy this function code,
to an actual lambda you must:

1) Run `mvn package`. It will build your poject and generate a JAR file in a `/target` directory at the root of the package called `lambda-jar-jar-with-dependencies.jar`
2) In the AWS console, create a Lambda function. Pick `Java 11` as the language and click Create.
3) Once created, in the Source Code tab, click on upload and upload the JAR generate in the `/target` directory
4) Once uploaded, click on `Edit` in the `Runtime settings` and point the Handler to the correct class/function (Which is `fares.lambda.java.HelloWorld::handleRequest`)
5) Hit Save, and all done! You should be able to execute the code

Note: If you want to do this process through CDK, follow: https://aws.amazon.com/blogs/opensource/packaging-and-deploying-aws-lambda-functions-written-in-java-with-aws-cloud-development-kit/