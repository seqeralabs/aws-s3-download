import com.amazonaws.regions.Regions;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class Helper {

    static String getRegion() {
        if( System.getenv("AWS_DEFAULT_REGION")!=null )
            return System.getenv("AWS_DEFAULT_REGION");

        if( System.getenv("AWS_REGION")!=null )
            return System.getenv("AWS_REGION");

        return Regions.DEFAULT_REGION.getName();
    }

}
