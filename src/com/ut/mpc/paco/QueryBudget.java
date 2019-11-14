package com.ut.mpc.paco;

/**
 * Created by nathanielwendt on 10/13/16.
 */
public class QueryBudget {
    private int budget;
    private int balance;

    public QueryBudget(int budget){
        this.budget = budget;
    }

    public void spend(int value){
        if(balance + value > budget) {
            throw new RuntimeException("Query Budget exceeded");
        }
        balance += value;
    }

    public boolean canSpend(int value){
        return balance + value <= budget;
    }
}
