package com.example.expensetracker.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Expense {
    private Long id;

    private String description;

    private ExpenseType type;

    private Double amount;

    private User debtor;

    private List<User> creditors = new ArrayList<>();

    private Boolean isGroupExpense;

    private Date createdDate;

    public Expense(Long id, String description, ExpenseType type, Double amount, User debtor, List<User> creditors, Boolean isGroupExpense) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.debtor = debtor;
        this.creditors = creditors;
        this.isGroupExpense = isGroupExpense;
    }

    public Expense(String description, ExpenseType type, Double amount, User debtor, List<User> creditors, Boolean isGroupExpense) {
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.debtor = debtor;
        this.creditors = creditors;
        this.isGroupExpense = isGroupExpense;
    }

    public Expense(Long id) {
        this.id = id;
    }

    public Expense() {
    }

    public Expense(String description, ExpenseType expenseType, Double amount) {
        this.description = description;
        this.type = expenseType;
        this.amount = amount;
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

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getDebtor() {
        return debtor;
    }

    public void setDebtor(User debtor) {
        this.debtor = debtor;
    }

    public List<User> getCreditors() {
        return creditors;
    }

    public void setCreditors(List<User> creditors) {
        this.creditors = creditors;
    }

    public Boolean getIsGroupExpense() {
        return isGroupExpense;
    }

    public void setIsGroupExpense(Boolean groupExpense) {
        isGroupExpense = groupExpense;
    }

    public Boolean getGroupExpense() {
        return isGroupExpense;
    }

    public void setGroupExpense(Boolean groupExpense) {
        isGroupExpense = groupExpense;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}

