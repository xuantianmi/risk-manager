package site.systek.pojo;

import java.io.Serializable;

public class HorseRisk implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6185713166608613281L;
    
    private Integer id;
    
    private Integer volume;
    
    private Integer award;
    
    public HorseRisk() {
        super();
    }
    
    public HorseRisk(Integer id, Integer volume, Integer award) {
        super();
        this.id = id;
        this.volume = volume;
        this.award = award;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getVolume() {
        return volume;
    }
    
    public void setVolume(Integer volume) {
        this.volume = volume;
    }
    
    public Integer getAward() {
        return award;
    }
    
    public void setAward(Integer award) {
        this.award = award;
    }
    
    @Override
    public String toString() {
        return "Horse [id=" + id + ", volume=" + volume + ", award=" + award + ", getId()=" + getId() + ", getVolume()="
                + getVolume() + ", getAward()=" + getAward() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
                + ", toString()=" + super.toString() + "]";
    }
}
