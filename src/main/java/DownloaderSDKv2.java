import com.amazonaws.s3.S3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.transfer.s3.FileDownload;
import software.amazon.awssdk.transfer.s3.S3ClientConfiguration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author Jordi Deu-Pons <jordi@seqera.io>
 */
public class DownloaderSDKv2 {

    private static final Logger log = LoggerFactory.getLogger(DownloaderSDKv2.class.getSimpleName());

    private DownloaderSDKv2() {
    }

    static void download(String targetFile, String bucket, String key, String region) {

        final S3TransferManager tm = S3TransferManager.builder()
                .s3ClientConfiguration(cfg -> configClient(cfg.region(Region.of(region))))
                .build();

        try {
            log.info("Starting download region={} bucket={} key={}", region, bucket, key);
            FileDownload download = tm.downloadFile(d -> d.getObjectRequest(g -> g.bucket(bucket).key(key)).destination(Paths.get(targetFile)));
            download.completionFuture().join();
            log.info("Done");
        } catch (S3Exception e) {
            log.error(e.getMessage());
            System.exit(1);
        }

    }

    static S3ClientConfiguration.Builder configClient(S3ClientConfiguration.Builder builder) {
        setIntegerValue("AWS_MAX_CONCURRENCY", builder::maxConcurrency);
        return builder;
    }

    static void setIntegerValue(String key, Consumer<Integer> consumer) {
        final Map<String, String> env = System.getenv();
        final Properties props = System.getProperties();
        final String propKey = key.toLowerCase().replace("aws_", "aws.");
        if (env.containsKey(key) || props.containsKey(propKey)) {
            String strVal = env.getOrDefault(key, props.getProperty(propKey));
            try {
                Integer value = Integer.parseInt(strVal);
                consumer.accept(value);
            } catch (NumberFormatException e) {
                log.error("Invalid number format value '{}' at '{}'", strVal, env.containsKey(key) ? key : propKey);
                System.exit(1);
            }
        }

    }
}
