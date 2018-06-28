package site.systek.pojo;

import java.io.Serializable;
import java.util.List;

public class HorseRiskRequest implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 554872283433048598L;
    
    private String drawId;
    
    private String operation;
    
    private List<HorseRisk> horseInfo;
    
    public HorseRiskRequest() {
        super();
    }
    
    public HorseRiskRequest(String drawId, String operation, List<HorseRisk> horseInfo) {
        super();
        this.drawId = drawId;
        this.operation = operation;
        this.horseInfo = horseInfo;
    }
    
    public String getDrawId() {
        return drawId;
    }
    
    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public List<HorseRisk> getHorseInfo() {
        return horseInfo;
    }
    
    public void setHorseInfo(List<HorseRisk> horseInfo) {
        this.horseInfo = horseInfo;
    }
    
    @Override
    public String toString() {
        return "HorseRiskRequest [drawId=" + drawId + ", operation=" + operation + ", horseInfo=" + horseInfo + "]";
    }
}
