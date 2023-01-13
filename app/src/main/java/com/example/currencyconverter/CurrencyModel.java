package com.example.currencyconverter;

import com.google.gson.annotations.SerializedName;

public class CurrencyModel {
    @SerializedName("currency")
    private String currency;

    public  CurrencyModel(String currency){
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
