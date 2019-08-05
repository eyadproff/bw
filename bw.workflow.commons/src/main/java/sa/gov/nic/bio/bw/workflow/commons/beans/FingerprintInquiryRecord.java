package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.io.InputStream;

public class FingerprintInquiryRecord extends JavaBean
{
    private Integer recordSourceSystem;
    private String recordId;
    private InputStream faceImage;
    private String name;
    private String translatedName;
    private String nationality;
    private String occupation;
    private String gender;
    private String personId;
    private String personIdType;
    private String documentId;
    private String documentIdType;
    private String documentIssuanceDate;
    private String documentExpiryDate;
    private String dateOfBirth;
    private String birthPlace;
    private String pageCounter;

    public Integer getRecordSourceSystem(){return recordSourceSystem;}
    public void setRecordSourceSystem(Integer recordSourceSystem){this.recordSourceSystem = recordSourceSystem;}

    public String getRecordId(){return recordId;}
    public void setRecordId(String recordId){this.recordId = recordId;}

    public InputStream getFaceImage(){return faceImage;}
    public void setFaceImage(InputStream faceImage){this.faceImage = faceImage;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getTranslatedName(){return translatedName;}
    public void setTranslatedName(String translatedName){this.translatedName = translatedName;}

    public String getNationality(){return nationality;}
    public void setNationality(String nationality){this.nationality = nationality;}

    public String getOccupation(){return occupation;}
    public void setOccupation(String occupation){this.occupation = occupation;}

    public String getGender(){return gender;}
    public void setGender(String gender){this.gender = gender;}

    public String getPersonId(){return personId;}
    public void setPersonId(String personId){this.personId = personId;}

    public String getPersonIdType(){return personIdType;}
    public void setPersonIdType(String personIdType){this.personIdType = personIdType;}

    public String getDocumentId(){return documentId;}
    public void setDocumentId(String documentId){this.documentId = documentId;}

    public String getDocumentIdType(){return documentIdType;}
    public void setDocumentIdType(String documentIdType){this.documentIdType = documentIdType;}

    public String getDocumentIssuanceDate(){return documentIssuanceDate;}
    public void setDocumentIssuanceDate(String documentIssuanceDate){this.documentIssuanceDate = documentIssuanceDate;}

    public String getDocumentExpiryDate(){return documentExpiryDate;}
    public void setDocumentExpiryDate(String documentExpiryDate){this.documentExpiryDate = documentExpiryDate;}

    public String getDateOfBirth(){return dateOfBirth;}
    public void setDateOfBirth(String dateOfBirth){this.dateOfBirth = dateOfBirth;}

    public String getBirthPlace(){return birthPlace;}
    public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}

    public String getPageCounter(){return pageCounter;}
    public void setPageCounter(String pageCounter){this.pageCounter = pageCounter;}

    @Override
    public String toString() {
        return "FingerprintInquiryRecord{" +
                "recordSourceSystem=" + recordSourceSystem +
                ", recordId='" + recordId + '\'' +
                ", faceImage=" + faceImage +
                ", name='" + name + '\'' +
                ", translatedName='" + translatedName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", occupation='" + occupation + '\'' +
                ", gender='" + gender + '\'' +
                ", personId='" + personId + '\'' +
                ", personIdType='" + personIdType + '\'' +
                ", documentId='" + documentId + '\'' +
                ", documentIdType='" + documentIdType + '\'' +
                ", documentIssuanceDate='" + documentIssuanceDate + '\'' +
                ", documentExpiryDate='" + documentExpiryDate + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", pageCounter='" + pageCounter + '\'' +
                '}';
    }
}