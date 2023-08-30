package fares.lambda.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Lambda handler
 */
public class HelloWorld implements RequestHandler
{
    @Override
    public Object handleRequest(Object o, Context context) {
        return "Hello world!";
    }
}
