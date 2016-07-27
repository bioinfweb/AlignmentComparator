/*
 * AlignmentComparator - Compare and annotate two alternative multiple sequence alignments
 * Copyright (C) 2014-2016  Ben Stöver
 * <http://bioinfweb.info/Software>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.alignmentcomparator.gui.comments;


import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;



public class CommentPositionerFactory {
  private static CommentPositionerFactory firstInstance = null;
  
  
  private Map<Class<? extends CommentPositioner>, CommentPositioner> map = 
  		new HashMap<Class<? extends CommentPositioner>, CommentPositioner>();
  
  
  private CommentPositionerFactory() {
  	super();
  	fillMap();
  }
  
  
  public static CommentPositionerFactory getInstance() {
  	if (firstInstance == null) {
  		firstInstance = new CommentPositionerFactory();
  	}
  	return firstInstance;
  }
  
  
  private void fillMap() {
  	map.put(SingleLineCommentPositioner.class, new SingleLineCommentPositioner());
  }
  
  
  public CommentPositioner getPositioner(Class<? extends CommentPositioner> positionerClass) {
  	return map.get(positionerClass);
  }
  
  
  public Collection<CommentPositioner> getAllPositioners() {
  	return map.values();
  }
}
