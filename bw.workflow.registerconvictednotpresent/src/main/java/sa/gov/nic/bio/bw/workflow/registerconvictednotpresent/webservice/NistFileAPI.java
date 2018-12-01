package sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.beans.CriminalNistFile;

public interface NistFileAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/nist/extraction/v1")
	Call<CriminalNistFile> extractDataFromNistFile(@Header("Workflow-Code") Integer workflowId,
	                                               @Header("Workflow-Tcn") Long workflowTcn,
	                                               @Field("nist") String nistFileBase64);
}