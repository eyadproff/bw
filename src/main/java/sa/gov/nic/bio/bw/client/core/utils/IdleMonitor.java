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

public class IdleMonitor
{
	private final Timeline beforeWarningTimeline = new Timeline();
	private final Timeline afterWarningTimeline = new Timeline();
	private final EventHandler<Event> userEventHandler;
	private int timerSeconds;
	
	public IdleMonitor(int beforeShowingIdleWarningSeconds, Runnable onShowingIdleWarning, int afterShowingIdleWarningSeconds, Runnable onIdle, Runnable onIdleInterrupt, Consumer<Integer> onTick, Node warningNode)
	{
		beforeWarningTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(beforeShowingIdleWarningSeconds), e ->
		{
			onShowingIdleWarning.run();
			timerSeconds = afterShowingIdleWarningSeconds;
			afterWarningTimeline.playFromStart();
		}));
		
		timerSeconds = afterShowingIdleWarningSeconds;
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
			   ((e.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET || e.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) && e.getTarget() instanceof Node && isParent(warningNode, (Node) e.getTarget())))
			{
				e.consume(); // prevent the event from continuing to bubble up the event dispatch chain
				return;
			}
			
			if(beforeWarningTimeline.getStatus() == Animation.Status.RUNNING || afterWarningTimeline.getStatus() == Animation.Status.RUNNING)
			{
				afterWarningTimeline.stop();
				beforeWarningTimeline.playFromStart();
				onIdleInterrupt.run();
			}
		};
	}
	
	private boolean isParent(Node parent, Node child)
	{
		Parent curr = child.getParent();
		
		while(curr != null)
		{
			if(curr == parent) return true;
			curr = curr.getParent();
		}
		
		return false;
	}
	
	public void register(Stage stage, EventType<? extends Event> eventType)
	{
		stage.addEventFilter(eventType, userEventHandler);
	}
	
	public void unregister(Stage stage, EventType<? extends Event> eventType)
	{
		stage.removeEventFilter(eventType, userEventHandler);
	}
	
	public void startMonitoring()
	{
		beforeWarningTimeline.playFromStart();
	}
	
	public void stopMonitoring()
	{
		beforeWarningTimeline.stop();
	}
}