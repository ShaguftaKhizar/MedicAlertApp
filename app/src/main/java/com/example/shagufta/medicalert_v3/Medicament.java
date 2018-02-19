package com.example.shagufta.medicalert_v3;

import com.cloudant.sync.documentstore.DocumentRevision;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shagufta on 11/02/2018.
 */

public class Medicament {

    public Medicament () {}

    public Medicament (String name) { this.setName(name); }

    public Medicament (String name, boolean morning, boolean afternoon, boolean noon, int duration, int pillNumberPerBox, int numberOfBox,
                      int numberPillMorning, int numberPillAfternoon, int numberPillNoon, int index)
    {
        this.setName(name);
        this.setCompleted(false);
        this.setType(DOC_TYPE);

        this.setMorning(morning);
        this.setAfternoon(afternoon);
        this.setNoon(noon);
        this.setDuration(duration);
        this.setPillNumberPerBox(pillNumberPerBox);
        this.setNumberOfBox(numberOfBox);
        this.setNumberPillMorning(numberPillMorning);
        this.setNumberPillAfternoon(numberPillAfternoon);
        this.setNumberPillNoon(numberPillNoon);
        this.setIndex(index);
    }

    private DocumentRevision rev;
    public DocumentRevision getDocumentRevision() { return rev; }

    static final String DOC_TYPE = "com.cloudant.sync.example.task";
    private String type = DOC_TYPE;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    private boolean completed;
    public boolean isCompleted() {
        return this.completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    private int index;
    public void setIndex(int id) { this.index = id; }

    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String desc) {
        this.name = desc;
    }

    private boolean morning;
    public void setMorning(boolean morning) { this.morning = morning; }
    public boolean getMorning() { return this.morning; }

    private boolean afternoon;
    public void setAfternoon(boolean afternoon) { this.afternoon = afternoon; }
    public boolean getAfternoon() { return this.afternoon; }

    private boolean noon;
    public void setNoon(boolean noon) { this.noon = noon; }
    public boolean getNoon() { return this.noon; }

    private int duration;
    public void setDuration(int duration) { this.duration = duration; }
    public int getDuration() { return this.duration; }

    private int pillNumberPerBox;
    public void setPillNumberPerBox(int pillNumberPerBox) { this.pillNumberPerBox = pillNumberPerBox; }
    public int getPillNumberPerBox() { return this.pillNumberPerBox; }

    private int numberOfBox;
    public void setNumberOfBox(int numberOfBox) { this.numberOfBox = numberOfBox; }
    public int getNumberOfBox() { return this.numberOfBox; }

    private int numberPillMorning;
    public void setNumberPillMorning(int numberPillMorning) { this.numberPillMorning = numberPillMorning; }
    public int getNumberPillMorning() {return this.numberPillMorning; }

    private int numberPillAfternoon;
    public void setNumberPillAfternoon(int numberPillAfternoon) { this.numberPillAfternoon = numberPillAfternoon; }
    public int getNumberPillAfternoon() { return this.numberPillAfternoon; }

    private int numberPillNoon;
    public void setNumberPillNoon(int numberPillNoon) { this.numberPillNoon = numberPillNoon; }
    public int getNumberPillNoon() { return this.numberPillNoon; }

    @Override
    public String toString() {
        return "{ name: " + getName() + ", completed: " + isCompleted() + "}";
    }

    public static Medicament fromRevision(DocumentRevision rev) {
        Medicament o = new Medicament();
        o.rev = rev;
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.getBody().asMap();
        if(map.containsKey("type") && map.get("type").equals(Medicament.DOC_TYPE)) {
            o.setType((String) map.get("type"));
            o.setCompleted((Boolean) map.get("completed"));
            o.setName((String) map.get("name"));

            o.setMorning((boolean) map.get("morning"));
            o.setAfternoon((boolean) map.get("afternoon"));
            o.setNoon((boolean) map.get("noon"));
            o.setDuration((int) map.get("duration"));
            o.setPillNumberPerBox((int) map.get("pillNumberPerBox"));
            o.setNumberOfBox((int) map.get("numberOfBox"));
            o.setNumberPillMorning((int) map.get("numberPillMorning"));
            o.setNumberPillAfternoon((int) map.get("numberPillAfternoon"));
            o.setNumberPillNoon((int) map.get("numberPillNoon"));
            o.setIndex((int) map.get("index"));

            return o;
        }
        return null;
    }

    public Map<String, Object> asMap() {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("completed", completed);
        map.put("name", name);

        map.put("morning", morning);
        map.put("afternoon", afternoon);
        map.put("noon", noon);
        map.put("duration", duration);
        map.put("pillNumberPerBox", pillNumberPerBox);
        map.put("numberOfBox", numberOfBox);
        map.put("numberPillMorning", numberPillMorning);
        map.put("numberPillAfternoon", numberPillAfternoon);
        map.put("numberPillNoon", numberPillNoon);
        map.put("index", index);

        return map;
    }
}
