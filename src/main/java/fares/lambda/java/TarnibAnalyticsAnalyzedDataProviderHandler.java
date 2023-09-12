package fares.lambda.java;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static fares.lambda.java.Constants.TARNIB_ANALYTICS_ANALYZED_DATA_DIRECTORY;
import static fares.lambda.java.Constants.TARNIB_ANALYTICS_S3_BUCKET;

public class TarnibAnalyticsAnalyzedDataProviderHandler implements RequestHandler {
    @Override
    public Object handleRequest(Object o, Context context) {

        // Initialize S3 client
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        try {

            // List objects in the specified S3 directory
            List<S3ObjectSummary> objectSummaries = s3Client.listObjects(TARNIB_ANALYTICS_S3_BUCKET, TARNIB_ANALYTICS_ANALYZED_DATA_DIRECTORY).getObjectSummaries();
            // There can only be 1 file, so get the first file
            S3Object s3Object = s3Client.getObject(TARNIB_ANALYTICS_S3_BUCKET, objectSummaries.get(0).getKey());
            // Read the JSON content from the S3Object
            String jsonString = readS3ObjectContent(s3Object.getObjectContent());
            // Parse the JSON content into a JSONObject
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonContent = objectMapper.readTree(jsonString);

            return jsonContent.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read S3 json file content and return it as string
     *
     * @param objectInputStream
     * @return
     * @throws IOException
     */
    private String readS3ObjectContent(S3ObjectInputStream objectInputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objectInputStream))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading S3 object content", e);
        }
    }
}
