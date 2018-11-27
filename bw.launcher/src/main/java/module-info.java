module bw.launcher
{
	requires bw.core;
	requires biokit.library;
	requires bio.commons;
	
	exports sa.gov.nic.bio.bw;
	exports sa.gov.nic.bio.bw.preloader;
	
	opens sa.gov.nic.bio.bw.preloader.beans to org.apache.commons.lang3;
}