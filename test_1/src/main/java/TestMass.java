import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMass {
    public static void main(String[] args) {
        Integer[] a ={1,2,4,4,2,3,4,1,7};
        Integer[] b ={1,2,4,4,2,3,9,1,7};
        Integer[] c ={1,2,7,1,2,3,1,7};

        //ArrayList<Integer> arr =  new   ArrayList<>(Arrays.asList(new Integer[] {1,2,4,4,2,3,1,7}));
        System.out.println(" -> OUT:" + Met_Arr(new ArrayList<> (Arrays.asList(a))));
        System.out.println(" -> OUT:" + Met_Arr(new ArrayList<> (Arrays.asList(b))));
        System.out.println(" -> OUT:" + Met_Arr(new ArrayList<> (Arrays.asList(c))));
    }

    /*
Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов, идущих после последней четверки.
Входной массив должен содержать хотя бы одну четверку, иначе в методе необходимо выбросить RuntimeException.
Написать набор тестов для этого метода (по 3-4 варианта входных данных).
Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
*/
    public static ArrayList<Integer> Met_Arr(ArrayList<Integer> Arr) {

        ArrayList<Integer> Res= new ArrayList<>();
       // System.out.print("IN:"+ Arr);
        for (int i = Arr.size()-1; i >-1; i--) {
            if (Arr.get(i)==4) return Res;
            Res.add(0,Arr.get(i));
        }
        throw new RuntimeException("Ошибка: в массиве нет элемента со значением 4");
    }


    /*
    Написать метод, который проверяет состав массива из чисел 1 и 4.
    Если в нем нет хоть одной четверки или единицы, то метод вернет false;
    Написать набор тестов для этого метода (по 3-4 варианта входных данных).
     */

    public static boolean Met_test(ArrayList<Integer> Arr) {
       boolean F_Four = false;
       boolean F_One = false;

        for (Integer i : Arr) {
            if (i==1) F_One=true;
            if (i==4) F_Four=true;
            if (F_One && F_Four) return true;
        }

       return false;
    }
}
