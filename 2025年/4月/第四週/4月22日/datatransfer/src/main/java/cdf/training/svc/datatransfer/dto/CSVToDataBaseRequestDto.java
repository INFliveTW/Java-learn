package cdf.training.svc.datatransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(example = "{\"COMPANY\": \"string\", \"EXCUTETIME\": \"string\"}")
public class CSVToDataBaseRequestDto {
    private String COMPANY;
    public String getCOMPANY() { return COMPANY; }
    public void setCOMPANY(String COMPANY) { this.COMPANY = COMPANY; }

    private String EXCUTETIME;
    public String getEXCUTETIME() { return EXCUTETIME; }
    public void setEXCUTETIME(String EXCUTETIME) { this.EXCUTETIME = EXCUTETIME; }
}
