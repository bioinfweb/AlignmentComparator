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
package info.bioinfweb.alignmentcomparator.document.comments;


import java.util.HashMap;
import java.util.Map;



public class CommentAnchorList implements CommentAnchor {
  private Map<String, SingleCommentAnchor> anchors = new HashMap<>();  //TODO Should an ID be introduced as the key, since alignments may be renamed?
	
  
  public SingleCommentAnchor getAnchor(String alignmentID) {
  	return anchors.get(alignmentID);
  	//TODO The properties of the anchor could be modified this may without being recognized by this class.
  }
  
  
  public void setAnchor(String alignmentID, SingleCommentAnchor anchor) {
  	anchors.put(alignmentID, anchor);
  }
  
  
	@Override
  public int getFirstPos() {  //TODO This implementation would not make sense, if the indices are not superindices!
  	if (anchors.isEmpty()) {
  		return -1;
  	}
  	else {
	  	int result = Integer.MAX_VALUE;
	  	for (String alignmentID : anchors.keySet()) {
				result = Math.min(anchors.get(alignmentID).getFirstPos(), result);
			}
	  	return result;
  	}
  }
  
  
	@Override
  public int getLastPos() {  //TODO This implementation would not make sense, if the indices are not superindices!
  	if (anchors.isEmpty()) {
  		return -1;
  	}
  	else {
	  	int result = Integer.MIN_VALUE;
	  	for (String alignmentID : anchors.keySet()) {
				result = Math.max(anchors.get(alignmentID).getLastPos(), result);
			}
	  	return result;
  	}
  }
}
