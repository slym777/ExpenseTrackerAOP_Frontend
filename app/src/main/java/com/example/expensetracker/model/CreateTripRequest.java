package com.example.expensetracker.model;

import java.util.ArrayList;
import java.util.List;

public class CreateTripRequest extends Trip {
    public CreateTripRequest() {
        super();
    }

    public CreateTripRequest(String name, String description, String avatarUri, String location, List<User> users) {
        super(name, description, avatarUri, location);
        this.users = users;
    }
}
