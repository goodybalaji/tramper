package org.tramper.synthesizer;
/*
import java.util.Locale;
import java.util.ResourceBundle;
*/

/**
 * Speech voice description
 * @author Paul-Emile
 */
public class VoiceDesc {
    /**  */
    private int age;
    /**  */
    private int gender;
    /**  */
    private String style;
    /**  */
    private String name;
    
    /**
     * 
     */
    public VoiceDesc() {
        super();
    }

    /**
     * @return age.
     */
    public int getAge() {
        return this.age;
    }
    /**
     * @param age age 
     */
    public void setAge(int age) {
        this.age = age;
    }
    /**
     * @return gender.
     */
    public int getGender() {
        return this.gender;
    }
    /**
     * @param gender gender 
     */
    public void setGender(int gender) {
        this.gender = gender;
    }
    /**
     * @return name.
     */
    public String getName() {
        return this.name;
    }
    /**
     * @param name name 
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return style.
     */
    public String getStyle() {
        return this.style;
    }
    
    /**
     * @param style style 
     */
    public void setStyle(String style) {
        this.style = style;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }
    
}
