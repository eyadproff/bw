package sa.gov.nic.bio.bw.workflow.citizenenrollment.beans;

import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.Date;
import java.util.List;

public class CitizenEnrollmentInfo {

    private Long personId;
    private Integer personType;
    private List<Finger> fingers;
    private List<Integer> missing;
    private String faceImage;
    private Date birthDate;
    private Integer gender;
    private String capturedRightIrisBase64;
    private String capturedLeftIrisBase64;
    // the last one added Exceptions if there is missing
    private Long supervisorId;


    public CitizenEnrollmentInfo(Long personId, Integer personType, List<Finger> fingers, List<Integer> missing,
                                 String faceImage, Date birthDate, Integer gender, String capturedRightIrisBase64,
                                 String capturedLeftIrisBase64, Long supervisorId) {
        this.personId = personId;
        this.personType = personType;
        this.fingers = fingers;
        this.missing = missing;
        this.faceImage = faceImage;
        this.birthDate = birthDate;
        this.gender = gender;
        this.capturedRightIrisBase64 = capturedRightIrisBase64;
        this.capturedLeftIrisBase64 = capturedLeftIrisBase64;
        this.supervisorId = supervisorId;
    }


    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }


    public List<Finger> getFingers() {
        return fingers;
    }

    public void setFingers(List<Finger> fingers) {
        this.fingers = fingers;
    }

    public List<Integer> getMissing() {
        return missing;
    }

    public void setMissing(List<Integer> missing) {
        this.missing = missing;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public void setPersonType(Integer personType) {
        this.personType = personType;
    }

    public Integer getPersonType() {
        return personType;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCapturedRightIrisBase64() {
        return capturedRightIrisBase64;
    }

    public void setCapturedRightIrisBase64(String capturedRightIrisBase64) {
        this.capturedRightIrisBase64 = capturedRightIrisBase64;
    }

    public String getCapturedLeftIrisBase64() {
        return capturedLeftIrisBase64;
    }

    public void setCapturedLeftIrisBase64(String capturedLeftIrisBase64) {
        this.capturedLeftIrisBase64 = capturedLeftIrisBase64;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }
}
