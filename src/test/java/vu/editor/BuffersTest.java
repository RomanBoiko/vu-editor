package vu.editor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

public class BuffersTest {
	Stack<Integer> stack;
	
	@Before public void setup() {
		stack = new Stack<Integer>();
		stack.push(1);
		stack.push(2);
		stack.push(3);
	}

	@Test public void iteratesStackInOrderFromYoungestTillOldest() {
		for (int i = stack.size()-1; i >=0; i--) {
			assertThat(stack.get(i), is(i+1));
		}
	}

	@Test public void getsItemByRowNumberFromStack() {
		int firstRow = 1;
		assertThat(stack.get(stack.size()-firstRow), is(3));
		int secondRow = 2;
		assertThat(stack.get(stack.size()-secondRow), is(2));
		int thirdRow = 3;
		assertThat(stack.get(stack.size()-thirdRow), is(1));
	}
	
	@Test public void movesItemToTheTop() {
		Integer removedItem = stack.remove(0);
		stack.push(removedItem);
		assertThat(stack.get(0), is(2));
		assertThat(stack.get(1), is(3));
		assertThat(stack.get(2), is(1));
	}
}
