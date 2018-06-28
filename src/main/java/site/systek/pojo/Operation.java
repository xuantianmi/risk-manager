package site.systek.pojo;

public enum Operation {
    MINUS("1", "扣除"), ADD("2", "增加");
    String code;
    
    String desc;
    
    /**
     * @param code
     * @param desc
     */
    private Operation(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
