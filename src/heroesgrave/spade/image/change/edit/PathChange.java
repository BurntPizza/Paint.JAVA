// {LICENSE}
/*
 * Copyright 2013-2015 HeroesGrave and other Spade developers.
 * 
 * This file is part of Spade
 * 
 * Spade is free software: you can redistribute it and/or modify
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

package heroesgrave.spade.image.change.edit;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.IEditChange;
import heroesgrave.spade.image.change.SingleChange;
import heroesgrave.spade.io.HistoryIO;
import heroesgrave.spade.io.Serialised;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PathChange implements IEditChange
{
	public static abstract class IPathChange extends SingleChange
	{
		public abstract void point(RawImage image, int x, int y, int colour);
		
		public abstract void line(RawImage image, int x1, int y1, int x2, int y2, int colour);
		
		public static void lineToPoints(IPathChange path, RawImage image, int x1, int y1, int x2, int y2, int colour)
		{
			final int dx = Math.abs(x2 - x1);
			final int dy = Math.abs(y2 - y1);
			final int sx = (x1 < x2) ? 1 : -1;
			final int sy = (y1 < y2) ? 1 : -1;
			int err = dx - dy;
			do
			{
				path.point(image, x1, y1, colour);
				final int e2 = 2 * err;
				if(e2 > -dy)
				{
					err = err - dy;
					x1 = x1 + sx;
				}
				if(e2 < dx)
				{
					err = err + dx;
					y1 = y1 + sy;
				}
			}
			while(!(x1 == x2 && y1 == y2));
			path.point(image, x2, y2, colour);
		}
	}
	
	protected IPathChange change;
	protected ArrayList<Point> points = new ArrayList<Point>();
	protected int colour;
	
	public PathChange(short x, short y, int colour, IPathChange change)
	{
		this(new Point(x, y), colour, change);
	}
	
	public PathChange(Point p, int colour, IPathChange change)
	{
		this.colour = colour;
		points.add(p);
		this.change = change;
	}
	
	public boolean moveTo(short x, short y)
	{
		return this.moveTo(new Point(x, y));
	}
	
	public boolean moveTo(Point p)
	{
		if(points.get(points.size() - 1).equals(p))
			return false;
		points.add(p);
		return true;
	}
	
	@Override
	public Serial encode()
	{
		short[] data = new short[points.size() * 2];
		int i = 0;
		for(Point p : points)
		{
			data[i++] = (short) p.x;
			data[i++] = (short) p.y;
		}
		return new Serial(data, colour, change.encode());
	}
	
	@Override
	public void apply(RawImage image)
	{
		if(points.size() == 1)
		{
			Point p = points.get(0);
			change.point(image, p.x, p.y, colour);
		}
		else
		{
			// Fill in lines first
			for(int i = 1; i < points.size(); i++)
			{
				Point p1 = points.get(i - 1);
				Point p2 = points.get(i);
				
				change.line(image, p1.x, p1.y, p2.x, p2.y, colour);
			}
			
			// Points after
			for(Point p : points)
			{
				change.point(image, p.x, p.y, colour);
			}
		}
	}
	
	public static class Serial implements Serialised
	{
		private short[] points;
		private int colour;
		private Serialised change;
		
		public Serial()
		{
			
		}
		
		public Serial(short[] data, int colour, Serialised change)
		{
			this.colour = colour;
			this.points = data;
			this.change = change;
		}
		
		@Override
		public PathChange decode()
		{
			PathChange change = new PathChange(points[0], points[1], colour, (IPathChange) this.change.decode());
			for(int i = 2; i < points.length; i += 2)
				change.moveTo(points[i], points[i + 1]);
			return change;
		}
		
		@Override
		public void write(DataOutputStream out) throws IOException
		{
			out.writeByte(HistoryIO.getChangeID(change.getClass()));
			change.write(out);
			out.writeInt(colour);
			out.writeInt(points.length);
			for(short s : points)
				out.writeShort(s);
		}
		
		@Override
		public void read(DataInputStream in) throws IOException
		{
			change = HistoryIO.create(in.readByte());
			change.read(in);
			colour = in.readInt();
			points = new short[in.readInt()];
			for(int i = 0; i < points.length; i++)
				points[i] = in.readShort();
		}
	}
}
