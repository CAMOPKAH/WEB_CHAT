import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestHome {

    @Test
    public void TestOne(){

        Integer[] a ={1,2,4,4,2,3,4,1,7};
        Integer[] b ={1,2,4,4,2,3,9,1,7};
        Integer[] c ={1,2,7,1,2,3,1,7};

        //ArrayList<Integer> arr =  new   ArrayList<>(Arrays.asList(new Integer[] {1,2,4,4,2,3,1,7}));


       Assert.assertEquals("BAD TEST 1",String.valueOf(TestMass.Met_Arr(new ArrayList<>(Arrays.asList(a))) ),"[1, 7]");
       Assert.assertEquals("BAD TEST 1",String.valueOf(TestMass.Met_Arr(new ArrayList<>(Arrays.asList(b))) ),"[2, 3, 9, 1, 7]");
      String  Err = null;
      try {
          TestMass.Met_Arr(new ArrayList<>(Arrays.asList(c)));
      }
      catch (Exception e) {
          Err=e.getMessage();
      }
      Assert.assertEquals("BAD TEST 3",  Err, "Ошибка: в массиве нет элемента со значением 4");


    }


    @Test
    public void TestTwo(){

        Integer[] a ={1,2,4,4,2,3,4,1,7};
        Integer[] b ={1,2,2,3,9,7,1};
        Integer[] c ={4,2,8,2,4,7};
        Integer[] d ={5,2,8,2,3,9,7};

        //ArrayList<Integer> arr =  new   ArrayList<>(Arrays.asList(new Integer[] {1,2,4,4,2,3,1,7}));
        Assert.assertEquals("BAD TEST 1",TestMass.Met_test(new ArrayList<>(Arrays.asList(a))),true);
        Assert.assertEquals("BAD TEST 2",TestMass.Met_test(new ArrayList<>(Arrays.asList(b))),false);
        Assert.assertEquals("BAD TEST 3",TestMass.Met_test(new ArrayList<>(Arrays.asList(c))),false);
        Assert.assertEquals("BAD TEST 1",TestMass.Met_test(new ArrayList<>(Arrays.asList(d))),false);



    }
}
