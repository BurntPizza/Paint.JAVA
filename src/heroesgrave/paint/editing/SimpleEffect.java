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

package heroesgrave.paint.editing;

import heroesgrave.paint.image.Layer;
import heroesgrave.paint.image.change.SingleChange;
import heroesgrave.paint.plugin.Plugin;

import java.net.URL;

public class SimpleEffect extends Effect
{
	private SingleChange change;
	private Class<? extends Plugin> class_;
	
	public SimpleEffect(Class<? extends Plugin> class_, String name, SingleChange change)
	{
		super(name);
		this.class_ = class_;
		this.change = change;
	}
	
	@Override
	public void perform(Layer layer)
	{
		layer.addChange(change);
	}
	
	@Override
	public URL getResource()
	{
		return this.class_.getResource("/res/icons/effects/" + name + ".png");
	}
}
