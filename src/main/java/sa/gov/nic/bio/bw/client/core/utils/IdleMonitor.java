package sa.gov.nic.bio.bw.client.core.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * A utility class that helps to detect idleness of user activity across the application GUI. When active,
 * any mouse or keyboard action will interrupt the idleness counter and start it over again.
 *
 * The idle monitor consists of two periods. The first period starts when the idle monitor as a whole started
 * and ends by the start of the second period. When the second period started, the warning notification message
 * should be shown on the GUI including a counter that counts until the end of the second period. Any activity
 * by the user should the interrupt the process and the first period starts over.
 *
 * @author Fouad Almalki
 */
public class IdleMonitor
{
	private static final Logger LOGGER = Logger.getLogger(IdleMonitor.class.getName());
	
	private final Timeline beforeWarningTimeline = new Timeline();
	private final Timeline afterWarningTimeline = new Timeline();
	private final EventHandler<Event> userEventHandler;
	private int timerSeconds;
	
	public IdleMonitor(Consumer<Integer> onShowingIdleWarning, Runnable onIdle, Runnable onIdleInterrupt,
	                   Consumer<Integer> onTick, Node warningNode)
	{
		String sIdleWarningBeforeSeconds = System.getProperty("jnlp.bio.bw.idle.warning.before.seconds");
		String sIdleWarningAfterSeconds = System.getProperty("jnlp.bio.bw.idle.warning.after.seconds");
		
		int idleWarningBeforeSeconds = 480; // default: 8 minutes
		int idleWarningAfterSeconds = 120; // default: 2 minutes
		
		if(sIdleWarningBeforeSeconds == null)
		{
			LOGGER.warning("idleWarningBeforeSeconds is null! Default value is " + idleWarningBeforeSeconds);
		}
		else try
		{
			idleWarningBeforeSeconds = Integer.parseInt(sIdleWarningBeforeSeconds);
			LOGGER.info("idleWarningBeforeSeconds = " + idleWarningBeforeSeconds);
		}
		catch(NumberFormatException e)
		{
			LOGGER.warning("Failed to parse sIdleWarningBeforeSeconds as int! sIdleWarningBeforeSeconds = " +
					       sIdleWarningBeforeSeconds);
		}
		
		if(sIdleWarningAfterSeconds == null)
		{
			LOGGER.warning("sIdleWarningAfterSeconds is null! Default value is " + idleWarningAfterSeconds);
		}
		else try
		{
			idleWarningAfterSeconds = Integer.parseInt(sIdleWarningAfterSeconds);
			LOGGER.info("idleWarningAfterSeconds = " + idleWarningAfterSeconds);
		}
		catch(NumberFormatException e)
		{
			LOGGER.warning("Failed to parse sIdleWarningAfterSeconds as int! sIdleWarningAfterSeconds = " +
					       sIdleWarningAfterSeconds);
		}
		
		int finalIdleWarningAfterSeconds = idleWarningAfterSeconds;
		beforeWarningTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(idleWarningBeforeSeconds), e ->
		{
			onShowingIdleWarning.accept(finalIdleWarningAfterSeconds);
			timerSeconds = finalIdleWarningAfterSeconds;
			afterWarningTimeline.playFromStart();
		}));
		
		timerSeconds = idleWarningAfterSeconds;
		afterWarningTimeline.setCycleCount(Animation.INDEFINITE);
		afterWarningTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event ->
		{
			if(--timerSeconds > 0) onTick.accept(timerSeconds);
			else
			{
				afterWarningTimeline.stop();
				onIdle.run();
			}
		}));
		
		this.userEventHandler = e ->
		{
			if(e.getEventType() == NotificationPane.ON_SHOWING ||
			   e.getEventType() == NotificationPane.ON_SHOWN ||
			   e.getEventType() == NotificationPane.ON_HIDING ||
			   e.getEventType() == NotificationPane.ON_HIDDEN ||
			   e.getTarget() == warningNode ||
				// handle the case when the mouse's position is the same as warningNode's position
			   ((e.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET ||
				 e.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) &&
			    e.getTarget() instanceof Node && isParent(warningNode, (Node) e.getTarget())))
			{
				e.consume(); // prevent the event from continuing to bubble up the event dispatch chain
				return;
			}
			
			if(beforeWarningTimeline.getStatus() == Animation.Status.RUNNING ||
			   afterWarningTimeline.getStatus() == Animation.Status.RUNNING)
			{
				afterWarningTimeline.stop();
				beforeWarningTimeline.playFromStart();
				onIdleInterrupt.run();
			}
		};
	}
	
	/**
	 * helper method to check if one JavaFX node is the parent of the other.
	 *
	 * @param parent the parent JavaFX node to test
	 * @param child the child JavaFX node to test
	 *
	 * @return <code>true</code> if <code>parent</code> is parent of  <code>child</code>, otherwise <code>false</code>
	 */
	private static boolean isParent(Node parent, Node child)
	{
		Parent curr = child.getParent();
		
		while(curr != null)
		{
			if(curr == parent) return true;
			curr = curr.getParent();
		}
		
		return false;
	}
	
	/**
	 * Register a JavaFX stage to monitored for idleness.
	 *
	 * @param stage the JavaFX stage to register
	 * @param eventType the type of event that to be registered which would interrupt the idleness counter
	 */
	public void register(Stage stage, EventType<? extends Event> eventType)
	{
		stage.addEventFilter(eventType, userEventHandler);
	}
	
	/**
	 * Unregister a JavaFX stage from being monitored for idleness.
	 *
	 * @param stage the JavaFX stage to unregister
	 * @param eventType the type of event that to be unregistered
	 */
	public void unregister(Stage stage, EventType<? extends Event> eventType)
	{
		stage.removeEventFilter(eventType, userEventHandler);
	}
	
	/**
	 * Start monitoring for idleness.
	 */
	public void startMonitoring()
	{
		beforeWarningTimeline.playFromStart();
	}
	
	/**
	 * Stop monitoring for idleness.
	 */
	public void stopMonitoring()
	{
		beforeWarningTimeline.stop();
		afterWarningTimeline.stop();
	}
}