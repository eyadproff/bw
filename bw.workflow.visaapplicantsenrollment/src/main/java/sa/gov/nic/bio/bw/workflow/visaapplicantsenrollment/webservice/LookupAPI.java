package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.*;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans.PassportTypeBean;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans.VisaTypeBean;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/visa-type/all/v2")
	Call<List<VisaTypeBean>> lookupVisaTypes();
	
	@GET("services-gateway-lookups/api/application/passport-types/v1")
	Call<List<PassportTypeBean>> lookupPassportTypes();
}