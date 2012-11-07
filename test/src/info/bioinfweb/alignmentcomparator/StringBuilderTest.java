package info.bioinfweb.alignmentcomparator;



public class StringBuilderTest {
  public static void main(String[] args) {
  	final int LENGTH = 6000;
  	StringBuffer buffer = new StringBuffer(LENGTH); 
    for (int i = 0; i < LENGTH; i++) {
			buffer.append("A");
		}
    String str = buffer.toString();
    System.out.println(str.charAt(5000));
    System.out.println(str);
	}
}
