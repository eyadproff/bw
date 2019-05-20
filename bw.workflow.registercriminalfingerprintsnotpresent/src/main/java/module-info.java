module bw.workflow.registercriminalfingerprintsnotpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.lib.jasperreports;
	requires bio.commons;
	requires biokit.library;
	requires bw.workflow.registerconvictedpresent;
	requires bw.workflow.registercriminalfingerprintspresent;
	requires bw.workflow.civilcriminalfingerprintsinquiry;
	
	opens sa.gov.nic.bio.bw.workflow.registercriminalfingerprintsnotpresent.bundles;
	opens sa.gov.nic.bio.bw.workflow.registercriminalfingerprintsnotpresent to bw.core;
}