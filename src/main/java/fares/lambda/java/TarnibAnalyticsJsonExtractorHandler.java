package fares.lambda.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import static fares.lambda.java.Constants.*;

/**
 * Lambda handler for TarnibAnalyticsJsonExtractor
 * Its job is to call the analytics API and save the response a json file in S3,
 * so it can be read by the analytics Glue jobs
 */
public class TarnibAnalyticsJsonExtractorHandler implements RequestHandler {
    /**
     * This is what gets executed when the lambda is invoked.
     */
    @Override
    public Object handleRequest(Object o, Context context) {

        try {
            // Create an HTTP client
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(TARNIB_ANALYTICS_API);

            // Execute the HTTP request
            CloseableHttpResponse response = httpClient.execute(httpGet);

            // Check if the response is successful (HTTP status code 200)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                // Check if the JSON response is not empty
                if (!jsonResponse.isEmpty()) {
                    // Upload the JSON response to S3
                    AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
                    s3Client.putObject(TARNIB_ANALYTICS_S3_BUCKET, TARNIB_ANALYTICS_RAW_JSON_S3_PATH, jsonResponse);

                    return String.format("Response JSON successfully saved in %s under %s", TARNIB_ANALYTICS_S3_BUCKET, TARNIB_ANALYTICS_RAW_JSON_S3_PATH);
                } else {
                    throw new Exception("Empty JSON response received from the API! Could not save anything to S3");
                }
            } else {
                throw new Exception(String.format("API request failed with status code: %d", response.getStatusLine().getStatusCode()));
            }
        } catch (Exception e) {
            return "An error has occurred! Error: " + e.getMessage();
        }
    }
}
