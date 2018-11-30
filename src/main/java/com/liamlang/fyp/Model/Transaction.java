package com.liamlang.fyp.Model;

import java.io.Serializable;

public class Transaction implements Serializable, Comparable {

    public String data;

    public Transaction() {
        this.data = "";
    }

    public Transaction(String data) {
        this.data = data;
    }

    public boolean equals(Transaction other) {
        // TODO keep this updated
        return this.data.equals(other.data);
    }

    @Override
    public int compareTo(Object o) {
        // TODO keep this updated
        if (!(o instanceof Transaction)) {
            return 0;
        }
        Transaction t = (Transaction) o;
        return this.data.compareTo(t.data);
    }

    public String toString() {
        return data;
    }
}
