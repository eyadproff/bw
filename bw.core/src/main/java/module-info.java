module bw.core
{
	uses sa.gov.nic.bio.bw.core.workflow.Workflow;
	
	requires static javafx.web;
	requires static javafx.media;
	requires static org.scenicview.scenicview;
	requires transitive java.logging;
	requires transitive java.prefs;
	requires transitive java.sql;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive javafx.swing;
	requires transitive bw.commons.resources;
	requires transitive controlsfx;
	requires transitive retrofit2;
	requires retrofit2.converter.gson;
	requires org.apache.commons.lang3;
	requires gson;
	requires okhttp3;
	requires biokit.library;
	requires bcl.utils;
	requires bio.commons;
	
	exports sa.gov.nic.bio.bw.core.workflow;
	exports sa.gov.nic.bio.bw.core;
	exports sa.gov.nic.bio.bw.core.beans;
	exports sa.gov.nic.bio.bw.core.biokit;
	exports sa.gov.nic.bio.bw.core.controllers;
	exports sa.gov.nic.bio.bw.core.interfaces;
	exports sa.gov.nic.bio.bw.core.utils;
	exports sa.gov.nic.bio.bw.core.webservice;
	opens sa.gov.nic.bio.bw.core.controllers to javafx.fxml;
	opens sa.gov.nic.bio.bw.core.wizard to javafx.fxml;
	opens sa.gov.nic.bio.bw.core.css;
	opens sa.gov.nic.bio.bw.core.beans to org.apache.commons.lang3;
	opens sa.gov.nic.bio.bw.core.utils to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.core.wizard;
	exports sa.gov.nic.bio.bw.core.tasks;
}