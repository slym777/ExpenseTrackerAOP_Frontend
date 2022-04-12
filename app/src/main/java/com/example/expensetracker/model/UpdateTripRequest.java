package com.example.expensetracker.model;

import java.util.ArrayList;
import java.util.List;

public class UpdateTripRequest extends Trip {
    public UpdateTripRequest() {
        super();
    }

    public UpdateTripRequest(String name, String description, String avatarUri, String location, List<User> users) {
        super(name, description, avatarUri, location);
        this.users = users;
    }
}
