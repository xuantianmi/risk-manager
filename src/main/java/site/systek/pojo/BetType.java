package site.systek.pojo;

/**
 * 投注类别枚举
 * 
 * @author Arielly
 *
 */
public enum BetType {
    
    STAKE(10, "投注"), CANCEL(99, "撤单");
    private Integer betTypeCode;
    
    private String betTypeName;
    
    private BetType(Integer betTypeCode, String betTypeName) {
        this.betTypeCode = betTypeCode;
        this.betTypeName = betTypeName;
    }
    
    public Integer getTypeCode() {
        return betTypeCode;
    }
    
    public void setTypeCode(Integer betTypeCode) {
        this.betTypeCode = betTypeCode;
    }
    
    public String getTypeName() {
        return betTypeName;
    }
    
    public void setTypeName(String betTypeName) {
        this.betTypeName = betTypeName;
    }
}
