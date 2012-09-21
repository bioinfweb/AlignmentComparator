package info.bioinfweb.alignmentcomparator.document.comments;



public class CommentPosition {
  private int firstPos;
  private int lastPos;
  
  
	public CommentPosition(int firstPos, int lastPos) {
		super();
		this.firstPos = firstPos;
		this.lastPos = lastPos;
	}
	
	
	public int getFirstPos() {
		return firstPos;
	}
	
	
	public int getLastPos() {
		return lastPos;
	}
}
