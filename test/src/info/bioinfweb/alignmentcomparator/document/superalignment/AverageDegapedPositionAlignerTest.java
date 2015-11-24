package info.bioinfweb.alignmentcomparator.document.superalignment;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.commons.testing.TestTools;

import org.junit.*;


import static org.junit.Assert.*;



public class AverageDegapedPositionAlignerTest {
	@Test
	public void test_alignPositions() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AverageDegapedPositionAligner aligner = new AverageDegapedPositionAligner();
		
		Map<String, Deque<Double>> unalignedPositions = new TreeMap<String, Deque<Double>>();
		Deque<Double> queue = new ArrayDeque<Double>();
		queue.add(0.1);
		queue.add(0.2);
		queue.add(0.25);
		queue.add(0.3);
		queue.add(0.6);
		queue.add(1.0);
		unalignedPositions.put("A", queue);
		
		queue = new ArrayDeque<Double>();
		queue.add(0.1);
		queue.add(0.2);
		queue.add(0.3);
		queue.add(0.4);
		queue.add(0.5);
		queue.add(0.7);
		queue.add(0.9);
		unalignedPositions.put("B", queue);
		
		@SuppressWarnings("unchecked")
		Map<String, List<Double>> alignesPositions = (Map<String, List<Double>>)TestTools.getPrivateMethod(
				AverageDegapedPositionAligner.class, "alignPositions", Map.class).invoke(aligner, unalignedPositions);
		
		List<Double> list = alignesPositions.get("A");
		assertEquals(0.1, list.get(0), 0.0000001);
		assertEquals(0.2, list.get(1), 0.0000001);
		assertEquals(0.25, list.get(2), 0.0000001);
		assertEquals(0.3, list.get(3), 0.0000001);
		assertTrue(Double.isNaN(list.get(4)));
		assertTrue(Double.isNaN(list.get(5)));
		assertEquals(0.6, list.get(6), 0.0000001);
		assertTrue(Double.isNaN(list.get(7)));
		assertTrue(Double.isNaN(list.get(8)));
		assertEquals(1.0, list.get(9), 0.0000001);
		
		list = alignesPositions.get("B");
		assertEquals(0.1, list.get(0), 0.0000001);
		assertEquals(0.2, list.get(1), 0.0000001);
		assertTrue(Double.isNaN(list.get(2)));
		assertEquals(0.3, list.get(3), 0.0000001);
		assertEquals(0.4, list.get(4), 0.0000001);
		assertEquals(0.5, list.get(5), 0.0000001);
		assertTrue(Double.isNaN(list.get(6)));
		assertEquals(0.7, list.get(7), 0.0000001);
		assertEquals(0.9, list.get(8), 0.0000001);
		assertTrue(Double.isNaN(list.get(9)));
		
//		for (int i = 0; i < alignesPositions.get("A").size(); i++) {
//			System.out.println(alignesPositions.get("A").get(i) + "\t" + alignesPositions.get("B").get(i));
//		}
	}
}
