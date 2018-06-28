package site.systek.pojo;

/**
 * 枚举: 交易标识
 * 
 * @author Arielly
 *
 */
public enum TransFlag {
    
    // 0 未交易, 4 交易失败, 9 交易成功
    NOT_TRANS(0, "未交易"), FAILURE(4, "交易失败"), SUCCESS(9, "交易成功");
    /**
     * 交易标识编码
     */
    private Integer codeValue;
    
    /**
     * 交易标识名称
     */
    private String codeName;
    
    private TransFlag() {
    }
    
    private TransFlag(Integer codeValue, String codeName) {
        this.codeValue = codeValue;
        this.codeName = codeName;
    }
    
    public Integer getCodeValue() {
        return codeValue;
    }
    
    public void setCodeValue(Integer codeValue) {
        this.codeValue = codeValue;
    }
    
    public String getCodeName() {
        return codeName;
    }
    
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}
