module bw.workflow.civilfingerprintsinquiry
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bcl.utils;
	requires biokit.library;
	requires bio.commons;
	requires bw.workflow.civilcriminalfingerprintsinquiry;
	
	opens sa.gov.nic.bio.bw.workflow.civilfingerprintsinquiry.bundles;
	opens sa.gov.nic.bio.bw.workflow.civilfingerprintsinquiry to bw.core;
}