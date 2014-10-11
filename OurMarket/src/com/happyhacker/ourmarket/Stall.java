package com.happyhacker.ourmarket;

public class Stall {
	public String name;
	public Member member;
	
	public Stall(String name, Member member){
		this.name = name;
		this.member = member;
	}
	
	public String toString(){
		return name + member;
	}
}
