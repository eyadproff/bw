package sa.gov.nic.bio.bw.workflow.cancellatent.webservice;

import retrofit2.Call;
import retrofit2.http.*;

public interface CancelLatentAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/latent/delink/v1")
	Call<Boolean> cancelLatent(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                           @Field("person-id") long personId, @Field("latent-id") String latentId);
}