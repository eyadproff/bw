module bw.core
{
	uses sa.gov.nic.bio.bw.core.workflow.Workflow;
	uses sa.gov.nic.bio.bw.core.spi.MyResourcesProvider;
	
	requires transitive java.logging;
	requires transitive java.prefs;
	requires transitive java.sql;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive javafx.swing;
	requires transitive controlsfx;
	requires transitive retrofit2;
	requires transitive bio.commons;
	requires retrofit2.converter.gson;
	requires gson;
	requires okhttp3;
	requires biokit.library;
	requires bcl.utils;
	
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
	opens sa.gov.nic.bio.bw.core.images;
	exports sa.gov.nic.bio.bw.core.spi;
	exports sa.gov.nic.bio.bw.core.wizard;
}