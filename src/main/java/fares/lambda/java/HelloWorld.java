package fares.lambda.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Lambda handler for TarnibAnalyticsJsonExtractor
 * Its job is to call the analytics API and save the response a json file in S3,
 * so it can be read by the analytics Glue jobs
 */
public class HelloWorld implements RequestHandler
{
    /**
     * This is what gets executed when the lambda is invoked.
     */
    @Override
    public Object handleRequest(Object o, Context context) {

        String apiUrl = "https://tarnib.net/api/getAnalytics";
        String s3BucketName = "tarnib-analytics-bucket";
        String s3ObjectName = "tarnib-analytics.json";

        try {
            // Create an HTTP client
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiUrl);

            // Execute the HTTP request
            String jsonResponse = EntityUtils.toString(httpClient.execute(httpGet).getEntity());

            // Upload the JSON response to S3
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            s3Client.putObject(s3BucketName, s3ObjectName, jsonResponse);

            return "JSON saved to S3 successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
