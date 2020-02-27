package com.project.gmall.cart.test;

import java.math.BigDecimal;

public class BigDecimalTest {
    public static void main(String[] args) {
        // 初始化
        BigDecimal b1 = new BigDecimal(0.01f);
        BigDecimal b2 = new BigDecimal(0.01d);
        BigDecimal b3 = new BigDecimal("0.01");
        System.out.println(b1+"\n"+b2+"\n"+b3);
        // 比较
        int i = b1.compareTo(b2);
        System.out.println(i);
        System.out.println();

        // 运算 add加  subtract减  multiply乘  divide除
        BigDecimal b4 = new BigDecimal("5");
        BigDecimal b5 = new BigDecimal("3");
        BigDecimal add = b4.add(b5);
        System.out.println(add);
        BigDecimal subtract = b4.subtract(b5);
        System.out.println(subtract);
        BigDecimal multiply = b4.multiply(b5);
        System.out.println(multiply);
//        BigDecimal divide = b4.divide(b5);
//        System.out.println(divide);

        // 取范围
        // 对运算进行取范围
        BigDecimal divide1 = b4.divide(b5,5,BigDecimal.ROUND_HALF_DOWN);
        System.out.println(divide1);

        // 对数据进行取范围
        BigDecimal add1 = b1.add(b2);
        BigDecimal bigDecimal = add1.setScale(5, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(bigDecimal);
    }
}
