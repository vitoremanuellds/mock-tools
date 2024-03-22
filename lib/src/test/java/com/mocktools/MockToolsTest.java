package com.mocktools;


import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class MockToolsTest {

    public class ParameterTest {
        private String privateStringTest;
        public int publicIntTest;
        private boolean privateBooleanTest;
    }

    public class Test {
        public ParameterTest parameterTest;
        private ParameterTest privateParameterTest;
        public double publicDoubleTest;
        private Float privateFloatWrapperTest;
        private String test;

    }

    @org.junit.jupiter.api.Test
    void testMyMethod() throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Test test = MockTools.populateUntilLevel(Test.class, 3);
        System.out.println("Deu certo!");
    }


}
