import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class DownloaderSDKv1 {

    private static final Logger log = LoggerFactory.getLogger(DownloaderSDKv1.class.getSimpleName());

    private DownloaderSDKv1() {
    }

    static void download(String targetFile, String bucket, String key, String region) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(buildClientConfiguration())
                .withRegion(region)
                .build();

        final TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3)
                .build();

        try {
            log.info("Starting download region={} bucket={} key={}", region, bucket, key);
            Download download = transferManager.download(bucket, key, new File(targetFile));
            download.waitForCompletion();
            log.info("Done");
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();
    }

    private static ClientConfiguration buildClientConfiguration() {
        final Map<String, String> env = System.getenv();
        final Properties props = System.getProperties();
        final ClientConfiguration config = new ClientConfiguration();

        if (env.containsKey("AWS_MAX_CONNECTIONS") || props.containsKey("aws.max_connections")) {
            String maxConnections = env.getOrDefault("AWS_MAX_CONNECTIONS", props.getProperty("aws.max_connections"));
            try {
                config.setMaxConnections(Integer.parseInt(maxConnections));
            } catch (NumberFormatException e) {
                log.error("Invalid number format max_connections value: {}", maxConnections);
                System.exit(1);
            }
        }
        return config;
    }
}
