// {LICENSE}
/*
 * Copyright 2013-2014 HeroesGrave and other Paint.JAVA developers.
 * 
 * This file is part of Paint.JAVA
 * 
 * Paint.JAVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package heroesgrave.paint.image;

import java.util.LinkedList;

public class History
{
	private LinkedList<Integer> changes = new LinkedList<Integer>();
	private LinkedList<Integer> reverted = new LinkedList<Integer>();
	
	private Document doc;
	
	public History(Document doc)
	{
		this.doc = doc;
	}
	
	public void addChange(int layerID)
	{
		reverted.clear();
		changes.push(layerID);
	}
	
	public void revertChange()
	{
		if(changes.isEmpty())
			return;
		int layerID = changes.pollLast();
		if(layerID != -1)
		{
			doc.getFlatMap().get(layerID).revertChange();
		}
		else
		{
			// Document Change
		}
		reverted.push(layerID);
	}
	
	public void repeatChange()
	{
		if(reverted.isEmpty())
			return;
		int layerID = reverted.pop();
		if(layerID != -1)
		{
			doc.getFlatMap().get(layerID).repeatChange();
		}
		else
		{
			// Document Change
		}
		changes.addLast(layerID);
	}
}
