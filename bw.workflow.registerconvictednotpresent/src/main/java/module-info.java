module bw.workflow.registerconvictednotpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.workflow.civilcriminalfingerprintsinquiry;
	requires bw.workflow.registerconvictedpresent;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.registerconvictednotpresent to bw.core;
}