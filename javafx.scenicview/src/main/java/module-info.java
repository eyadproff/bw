module javafx.scenicview
{
	requires javafx.base;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.swing;
	requires java.instrument;
	requires java.rmi;
	requires jdk.attach;
	requires java.logging;
	//requires javafx.web;
	
	opens org.scenicview to javafx.graphics;
	opens org.scenicview.view.cssfx to javafx.fxml;
	opens org.fxconnector.remote to java.rmi;
	exports org.scenicview;
}