import com.globallogic.test.tree.impl.CustomTree;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;


public class TestCustomTree {

    @Test
    public void testSize() {
        CustomTree<String, Integer> st = new CustomTree<>();
        assertEquals(0, st.size());
        st.put("Test String1", 1);
        st.put("Test String2", 2);
        st.put("Test String3", 3);
        assertEquals(3, st.size());
        st.remove("Test String2");
        st.remove("Test String3");
        assertEquals(1, st.size());
    }

    @Test
    public void testGetAndPut() {
        CustomTree<String, Integer> st = new CustomTree<>();
        try {
            st.put(null, 1);
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
        st.put("Test String1", 1);
        assertEquals(new Integer(1), st.get("Test String1"));
    }

    @Test
    public void testRemove() {
        CustomTree<String, Integer> st = new CustomTree<>();
        st.put("Test String1", 1);
        st.put("Test String2", 2);
        st.remove("Test String1");
        st.remove("Test String2");
        assertEquals(0, st.size());
    }

    @Test
    public void testIsEmpty() {
        CustomTree<String, Integer> st = new CustomTree<>();
        assertTrue(st.isEmpty());
        st.put("Test String1", 1);
        assertFalse(st.isEmpty());
    }

    @Test
    public void testContainsKey() {
        CustomTree<String, Integer> st = new CustomTree<>();
        assertFalse(st.containsKey("Test String1"));
        st.put("Test String1", 1);
        assertTrue(st.containsKey("Test String1"));
    }

    @Test
    public void testContainsValue() {
        CustomTree<String, Integer> st = new CustomTree<>();
        assertFalse(st.containsValue(1));
        st.put("Test String1", 1);
        assertTrue(st.containsValue(1));
    }

    @Test
    public void testTreeIteration() {
        CustomTree<Integer, String> st = new CustomTree<>();
        st.put(20, "first");
        st.put(3, "second");
        st.put(4, "third");
        st.put(6, "forth");
        st.put(7, "fifth");
        st.put(30, "sixth");
        st.put(55, "seventh");
        st.put(18, "eighth");

        Integer[] expected = {3, 4, 6, 7, 18, 20, 30, 55};
        Integer[] result = new Integer[8];
        int i=0;
        for(Integer key : st){
            result[i] = key;
            assertEquals(expected[i], result[i]);
            i++;
        }
    }
}
