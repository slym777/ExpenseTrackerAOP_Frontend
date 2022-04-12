package com.example.expensetracker.model;

import java.util.ArrayList;
import java.util.List;

public class Trip {

    protected Long id;
    protected String name;
    protected String description;
    protected String avatarUri;
    protected String location;
    protected Integer groupSize;
    protected List<User> users = new ArrayList<>();
    protected List<Expense> expenses = new ArrayList<>();

    public Trip() {
    }

    public Trip(Long id, String name, String description, String avatarUri, String location, Integer groupSize) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarUri = avatarUri;
        this.location = location;
        this.groupSize = groupSize;
    }

    public Trip(String name, String description, String avatarUri, String location) {
        this.name = name;
        this.description = description;
        this.avatarUri = avatarUri;
        this.location = location;
    }

    public Trip(Long id, String name, String description, String avatarUri, String location, Integer groupSize, List<User> users, List<Expense> expenses) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarUri = avatarUri;
        this.location = location;
        this.groupSize = groupSize;
        this.users = users;
        this.expenses = expenses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}