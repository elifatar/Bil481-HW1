package com.acme;

public class A {
    static void m1() {
        m2();
        m2();
    }

    static void m2() {
        B.m1();
        B.m1();
    }
}

class B {
    static void m1() {
        A.m1();
    }

    static void m2() {
        C.m3();
        D.m10();
        D.m1();
    }
}

class C {
    static void m1() {
        D.m1();
    }
}
class D {
    static void m1() {
        C.m3();
    }
}
