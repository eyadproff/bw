package sa.gov.nic.bio.bw.workflow.biometricsexception.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;


public class Cause extends JavaBean implements LocalizedText {

    private Integer causeId;
    private String descriptionEn;
    private String descriptionAr;


    public Integer getCauseId() {
        return causeId;
    }

    public void setCauseId(Integer causeId) {
        this.causeId = causeId;
    }


    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }


    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    @Override
    public String getArabicText() {
        return descriptionAr;
    }

    @Override
    public String getEnglishText() {
        return descriptionEn;
    }

}
