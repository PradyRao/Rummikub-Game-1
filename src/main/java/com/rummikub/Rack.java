package com.rummikub;

import java.util.ArrayList;

import com.rummikub.Tile;

public class Rack 
{
	private ArrayList<Tile> rack = new ArrayList<Tile>();

	public int getSize() {
		// TODO Auto-generated method stub
		return rack.size();
	}
	
	public ArrayList<Tile> getRackArray()
	{
		return rack;
		
	}

}
