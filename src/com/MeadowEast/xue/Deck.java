package com.MeadowEast.xue;

import java.util.LinkedList;

public class Deck {
	private LinkedList<CardStatus> cardStatusQueue = new LinkedList<CardStatus>();
	// Get a random deck of the specified size
	public Deck() {}
	public CardStatus get(){
		return cardStatusQueue.poll();
	}
	public void put(CardStatus cs){
		cardStatusQueue.add(cs);
	}
	public boolean isEmpty(){
		return cardStatusQueue.isEmpty();
	}
	public int size(){
		return cardStatusQueue.size();
	}
	public void remove(CardStatus cs){
		cardStatusQueue.remove(cs);
	}
	
	public boolean contains(CardStatus cs){
		for(CardStatus stat : cardStatusQueue){
			if ( stat.equals(cs) ){
				return true;
			}
		}
		return false;
	}
}
