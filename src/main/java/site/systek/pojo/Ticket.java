package site.systek.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket {
    
    private String name;
    
    private String id;
    
    private Integer age;
    
    @JsonProperty(value = "aaa")
    public String getName() {
        return name;
    }
    
    @JsonProperty(value = "aaa")
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonIgnore
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Ticket() {
    }
    
    public Ticket(String name, String id, Integer age) {
        this.name = name;
        this.id = id;
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", age=" + age + '}';
    	//return "{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", age=" + age + '}';
    }
}