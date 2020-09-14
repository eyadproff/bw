package sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans.Candidate;

import java.util.List;

public interface SearchByFaceImageAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/face/search/v1")
	Call<List<Candidate>> searchByFaceImage(@Header("Workflow-Code") Integer workflowId,
	                                        @Header("Workflow-Tcn") Long workflowTcn,
	                                        @Field("image") String imageBase64);
}