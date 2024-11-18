package dev.osunolimits.models;

import lombok.Data;

@Data
public class Group {
    private int id;
    private String name;
    private String emoji;
    private String desc;
    private int users;
}
