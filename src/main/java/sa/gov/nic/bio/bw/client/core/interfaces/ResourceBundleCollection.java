package sa.gov.nic.bio.bw.client.core.interfaces;

/**
 * An interface to locate the resource bundles that is used by a JavaFX controller. That includes labels and
 * messages resource bundles.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public interface ResourceBundleCollection
{
	String getLabelsBundlePath();
	String getMessagesBundlePath();
}