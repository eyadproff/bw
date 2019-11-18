package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.commons.beans.NistFileResponse;

public interface NistFileAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/criminal/nist/generation/v1")
	Call<NistFileResponse> generateNistFile(@Header("Workflow-Code") Integer workflowId,
											@Header("Workflow-Tcn") Long workflowTcn,
											@Field("fingers") String fingers,
											@Field("palms") String palms,
											@Field("missing") String missings);
}