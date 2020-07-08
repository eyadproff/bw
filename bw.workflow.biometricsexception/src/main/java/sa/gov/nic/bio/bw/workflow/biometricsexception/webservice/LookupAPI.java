package sa.gov.nic.bio.bw.workflow.biometricsexception.webservice;

import retrofit2.Call;
import retrofit2.http.*;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Cause;

import java.util.List;

public interface LookupAPI {

    @GET("services-gateway-lookups/api/bio/exclusive/causes/v1")
    Call<List<Cause>> lookupCauses();
}
