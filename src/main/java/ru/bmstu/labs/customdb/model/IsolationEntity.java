package ru.bmstu.labs.customdb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(value = {"locked"})
public class IsolationEntity {

    private boolean isLocked;
    private String lockedBy;

    public void locked(String alias) {
        isLocked = true;
        lockedBy = alias;
    }

    public void unlocked() {
        isLocked = false;
        lockedBy = "";
    }
}
