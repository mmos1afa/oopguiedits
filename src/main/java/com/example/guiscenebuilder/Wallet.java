package com.example.guiscenebuilder;

public class Wallet {
    private double balance;

    public Wallet(double balance)
    {
        this.balance = balance;
    }
    public Wallet() {
        this.balance = 0;
    }

    public double getBalance()
    {
        return balance;
    }

    public double withDraw(double amount)
    {
        System.out.println("Amount was withdrawn sucessfully");
        balance -= amount;
        return balance;
    }


    public double deposit(double amount){

        return balance += amount ;
    }

    @Override
    public String toString()
    {
        return "Wallet Balance =" + balance;
    }
}

