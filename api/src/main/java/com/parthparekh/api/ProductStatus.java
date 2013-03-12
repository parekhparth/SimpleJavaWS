package com.parthparekh.api;

/**
 * Product status enum
 *
 * @author: Parth Parekh
 **/
public enum ProductStatus {
    ACTIVE,
    DISABLED;

    public String getStatus() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}