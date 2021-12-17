import java.util.logging.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class Main1 {
    private static final Logger log = Logger.getLogger(Main1.class.getSimpleName());

    public static void main(String[ ] args ) {
        if( args.length== 0 ) {
            System.out.println("Missing source file");
            System.exit(1);
        }

        if( !args[0].startsWith("s3://") ) {
            System.out.println("Download file should start with s3://");
            System.exit(1);
        }

        if( args.length==1 )  {
            System.out.println("Missing target file");
            System.exit(1);
        }

        String bucket = args[0].substring(5).split("/")[0];
        String prefix = "s3://" + bucket + "/";
        String key = args[0].substring(prefix.length());

        String region = System.getenv("AWS_DEFAULT_REGION");
        if( region == null ) {
            region = "eu-west-1";
            System.out.println("Using AWS region eu-west-1. Use variable AWS_DEFAULT_REGION to change it");
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();

        java.io.File f = new java.io.File(args[1]);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
        try {
            System.out.println(String.format("Starting download region=%s bucket=%s key=%s", region, bucket, key));
            Download download = xfer_mgr.download(bucket, key, f);
            download.waitForCompletion();
            System.out.println(String.format("Done", bucket, key));
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();


    }


}
