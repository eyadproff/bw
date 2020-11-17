package sa.gov.nic.bio.bw.workflow.criminalclearancereport.webservice;

import retrofit2.Call;
import retrofit2.http.*;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;

public interface CriminalClearanceAPI {

    @FormUrlEncoded
    @POST("services-gateway-demographic/api/non-criminal/record/submit/v1")
    Call<CriminalClearanceReport> submitNonCriminalRecord(@Header("Workflow-Code") Integer workflowId,
            @Header("Workflow-Tcn") Long workflowTcn, @Field("noncriminal-record") String strNonCriminalRecord);


    @GET("services-gateway-demographic/api/non-criminal/record/retrieve/v1")
    Call<CriminalClearanceReport> getNonCriminalRecordBySamisId(@Header("Workflow-Code") Integer workflowId,
            @Header("Workflow-Tcn") Long workflowTcn, @Query("samis-id") Long samisId);


    @GET("services-gateway-demographic/api/non-criminal/record/retrieve/v2")
    Call<CriminalClearanceReport> getNonCriminalRecordByReportNumber(@Header("Workflow-Code") Integer workflowId,
            @Header("Workflow-Tcn") Long workflowTcn, @Query("report-number") Long reportNumber);
}
