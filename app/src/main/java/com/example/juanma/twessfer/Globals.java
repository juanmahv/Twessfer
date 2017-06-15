package com.example.juanma.twessfer;

public class Globals{
    private static Globals instance;

    // Global variable
    private Tweet currentTweet;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setCurrentTweet(Tweet t){
        this.currentTweet=t;
    }
    public Tweet getCurrentTweet(){
        return this.currentTweet;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}