package io.seqera.s3.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 * @author Jordi Deu-Pons <jordi@seqera.io>
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("Missing source file");
            System.exit(1);
        }

        if (!args[0].startsWith("s3://")) {
            log.error("Download file should start with s3://");
            System.exit(1);
        }

        if (args.length == 1) {
            log.error("Missing target file");
            System.exit(1);
        }

        String targetFile = args[1];
        String bucket = args[0].substring(5).split("/")[0];
        String prefix = "s3://" + bucket + "/";
        String key = args[0].substring(prefix.length());

        String region = System.getenv("AWS_DEFAULT_REGION");
        if (region == null) {
            region = "eu-west-1";
            log.warn("Using AWS region eu-west-1. Use variable AWS_DEFAULT_REGION to change it");
        }

        String sdk = System.getenv("AWS_SDK_VERSION");
        if (sdk == null) {
            sdk = "1";
            log.warn("Using AWS SDK v1. Set variable AWS_SDK_VERSION=2 if you want to use version 2.");
        }

        if (sdk.equals("1")) {
            DownloaderSDKv1.download(targetFile, bucket, key, region);
        } else if (sdk.equals("2")) {
            DownloaderSDKv2.download(targetFile, bucket, key, region);
        } else {
            log.error("Unknown SDK version {}. Set variable AWS_SDK_VERSION to 1 or 2", sdk);
            System.exit(1);
        }
    }

}
