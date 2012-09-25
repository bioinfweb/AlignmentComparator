package info.bioinfweb.alignmentcomparator.document.undo;


import info.bioinfweb.alignmentcomparator.document.Document;
import info.bioinfweb.alignmentcomparator.document.comments.Comment;



public abstract class AddRemoveCommentEdit extends CommentEdit {
  public AddRemoveCommentEdit(Document document, Comment comment) {
		super(document, comment);
	}


	public void add() {
		getDocument().getComments().add(getComment());
	}
	
	
	public void remove() {
		getDocument().getComments().remove(getComment());
  }
}
