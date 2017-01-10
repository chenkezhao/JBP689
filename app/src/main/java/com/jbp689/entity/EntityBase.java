package com.jbp689.entity;

import org.xutils.db.annotation.Column;

import java.io.Serializable;

public abstract class EntityBase implements Serializable, Cloneable {

    @Column(name = "id", isId = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
