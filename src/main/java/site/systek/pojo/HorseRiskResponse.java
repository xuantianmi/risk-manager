package site.systek.pojo;

import java.io.Serializable;
import java.util.Hashtable;

public class HorseRiskResponse implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1715766058400927054L;
    
    private String drawId;
    
    private boolean success;
    
    private Hashtable<String, HorseRisk> horseRiskInfo;
    
    public HorseRiskResponse() {
        super();
    }
    
    public HorseRiskResponse(String drawId, boolean success, Hashtable<String, HorseRisk> horseRiskInfo) {
        super();
        this.drawId = drawId;
        this.success = success;
        this.horseRiskInfo = horseRiskInfo;
    }
    
    public String getDrawId() {
        return drawId;
    }
    
    public HorseRiskResponse setDrawId(String drawId) {
        this.drawId = drawId;
        return this;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public HorseRiskResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }
    
    public Hashtable<String, HorseRisk> getHorseRiskInfo() {
        return horseRiskInfo;
    }
    
    public HorseRiskResponse setHorseRiskInfo(Hashtable<String, HorseRisk> horseRiskInfo) {
        this.horseRiskInfo = horseRiskInfo;
        return this;
    }
    
    @Override
    public String toString() {
        return "HorseRiskResponse [drawId=" + drawId + ", success=" + success + ", horseRiskInfo=" + horseRiskInfo + "]";
    }
}
