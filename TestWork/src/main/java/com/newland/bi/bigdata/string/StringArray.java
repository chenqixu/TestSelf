package com.newland.bi.bigdata.string;

/**
 * StringArray
 *
 * @author chenqixu
 */
public class StringArray {
    public static void main(String[] args) {
        new StringArray().arrayDeal();
    }

    public void arrayDeal() {
        String[] arr = {"a", "b", "c"};
        System.out.println("length：" + arr.length);
        for (int i = 0; i < arr.length; i++) {
            if ((i + 1) < arr.length)
                System.out.println(i + " " + arr[i] + ",");
            else
                System.out.println(i + " " + arr[i]);
        }
    }
}
