package site.systek.pojo;

import java.io.Serializable;

public class Stake implements Serializable {
    
    private static final long serialVersionUID = -4931823460455273147L;
    
    /**
     * 投注序列号
     */
    private Long stakeId;
    
    /**
     * 投注账户序列
     */
    private String accountId;
    
    /**
     * 奖期序列
     */
    private String drawId;
    
    /**
     * 玩法序列
     */
    private Integer playId;
    
    /**
     * 投注类别(10 投注，99 撤单)
     */
    private Integer betType = BetType.STAKE.getTypeCode();
    
    /**
     * 投注信息
     */
    private String betInfo;
    
    /**
     * 投注金额
     */
    private Long betAmount;
    
    /**
     * 投注时间
     */
    private String betTime;
    
    /**
     * 交易标识(默认0 未交易, 4 交易失败, 9 交易成功)
     */
    private Integer transFlag = TransFlag.NOT_TRANS.getCodeValue();
    
    /**
     * 交易时间(最后一次发起交易请求的时间)
     */
    private String transTime;
    
    public Stake() {
        super();
    }
    
    public Long getStakeId() {
        return stakeId;
    }
    
    public void setStakeId(Long stakeId) {
        this.stakeId = stakeId;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getDrawId() {
        return drawId;
    }
    
    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }
    
    public Integer getPlayId() {
        return playId;
    }
    
    public void setPlayId(Integer playId) {
        this.playId = playId;
    }
    
    public Integer getBetType() {
        return betType;
    }
    
    public void setBetType(Integer betType) {
        this.betType = betType;
    }
    
    public String getBetInfo() {
        return betInfo;
    }
    
    public void setBetInfo(String betInfo) {
        this.betInfo = betInfo;
    }
    
    public Long getBetAmount() {
        return betAmount;
    }
    
    public void setBetAmount(Long betAmount) {
        this.betAmount = betAmount;
    }
    
    public String getBetTime() {
        return betTime;
    }
    
    public void setBetTime(String betTime) {
        this.betTime = betTime;
    }
    
    public Integer getTransFlag() {
        return transFlag;
    }
    
    public void setTransFlag(Integer transFlag) {
        this.transFlag = transFlag;
    }
    
    public String getTransTime() {
        return transTime;
    }
    
    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }
    
    @Override
    public String toString() {
        return "Stake [stakeId=" + stakeId + ", accountId=" + accountId + ", drawId=" + drawId + ", playId=" + playId
                + ", betType=" + betType + ", betInfo=" + betInfo + ", betAmount=" + betAmount + ", betTime=" + betTime
                + ", transFlag=" + transFlag + ", transTime=" + transTime + "]";
    }
}
