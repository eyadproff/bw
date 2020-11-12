package sa.gov.nic.bio.bw.workflow.biometricsverification.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

public interface FingerprintVerificationAPI {
    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/fingerprint/verify/v1")
    Call<PersonInfo> verifyFingerprint(@Header("Workflow-Code") Integer workflowId,
            @Header("Workflow-Tcn") Long workflowTcn,
            @Field("person-id") Long personId,
            @Field("fingerprint") String fingerprint,
            @Field("finger-type") Integer fingerType);
}