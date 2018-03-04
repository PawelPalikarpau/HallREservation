package my.project.hallreservation.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Day {
    private LocalDateTime day;
    private List<LocalDateTime> hours;
    private List<String> leftInfo;
    private String topInfo;

    public LocalDateTime getDay() {
        return day;
    }

    public void setDay(LocalDateTime day) {
        this.day = day;
    }

    public List<LocalDateTime> getHours() {
        return hours;
    }

    public void setHours(List<LocalDateTime> hours) {
        this.hours = hours;
    }

    public void addHour(LocalDateTime hour) {
        this.hours.add(hour);
    }

    public List<String> getLeftInfo() {
        return leftInfo;
    }

    public void setLeftInfo(List<String> leftInfo) {
        this.leftInfo = leftInfo;
    }

    public void addLeftInfo(String leftInfo) {
        this.leftInfo.add(leftInfo);
    }

    public String getTopInfo() {
        return topInfo;
    }

    public void setTopInfo(String topInfo) {
        this.topInfo = topInfo;
    }
}
