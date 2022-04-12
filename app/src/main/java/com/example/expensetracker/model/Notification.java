package com.example.expensetracker.model;

import java.util.Date;

public class Notification {
    private Long id;
    private String description;
    private ActionType type;
    private Date createdDate;

    public Notification(Long id, String description, ActionType type, Date createdDate) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.createdDate = createdDate;
    }

    public Notification(String description, ActionType type, Date createdDate) {
        this.description = description;
        this.type = type;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
