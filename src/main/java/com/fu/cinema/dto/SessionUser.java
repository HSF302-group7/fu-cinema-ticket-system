package com.fu.cinema.dto;

import com.fu.cinema.enums.Role;
import lombok.Value;

import java.io.Serializable;

@Value
public class SessionUser implements Serializable {

    Long accountId;
    Long customerId;
    String displayName;
    Role role;

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }
}
