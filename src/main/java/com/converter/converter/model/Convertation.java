package com.converter.converter.model;

import javax.persistence.*;

@Entity
public class Convertation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String before;
    private String after;
    private double summBefore;
    private double summafter;
    private String date;
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_before_id")
    private Currency currencyBefore;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_after_id")
    private Currency currencyAfter;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Currency getCurrencyBefore() {
        return currencyBefore;
    }

    public void setCurrencyBefore(Currency currencyBefore) {
        this.currencyBefore = currencyBefore;
    }

    public Currency getCurrencyAfter() {
        return currencyAfter;
    }

    public void setCurrencyAfter(Currency currencyAfter) {
        this.currencyAfter = currencyAfter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public double getSummBefore() {
        return summBefore;
    }

    public void setSummBefore(double summBefore) {
        this.summBefore = summBefore;
    }

    public double getSummafter() {
        return summafter;
    }

    public void setSummafter(double summafter) {
        this.summafter = summafter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String user) {
        this.username = user;
    }
}
