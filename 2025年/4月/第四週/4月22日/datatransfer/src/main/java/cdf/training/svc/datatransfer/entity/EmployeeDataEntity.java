package cdf.training.svc.datatransfer.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmployeeDataEntity {
    private String ID;
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    private String DEPARTMENT;
    public String getDEPARTMENT() {
        return DEPARTMENT;
    }
    public void setDEPARTMENT(String DEPARTMENT) {
        this.DEPARTMENT = DEPARTMENT;
    }
    private String JOB_TITLE;
    public String getJOB_TITLE() {
        return JOB_TITLE;
    }
    public void setJOB_TITLE(String JOB_TITLE) {
        this.JOB_TITLE = JOB_TITLE;
    }
    private String NAME;
    public String getNAME() {
        return NAME;
    }
    public void setNAME(String NAME) {
        this.NAME = NAME;
    }
    private String TEL;
    public String getTEL() {
        return TEL;
    }
    public void setTEL(String TEL) {
        this.TEL = TEL;
    }
    private String EMAIL;
    public String getEMAIL() {
        return EMAIL;
    }
    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }
    private String COMPANY;
    public String getCOMPANY() {
        return COMPANY;
    }
    public void setCOMPANY(String COMPANY) {
        this.COMPANY = COMPANY;
    }
    private String EXCUTETIME;
    public String getEXCUTETIME() {
        return EXCUTETIME;
    }
    public void setEXCUTETIME(String EXCUTETIME) {
        this.EXCUTETIME = EXCUTETIME;
    }    
}
