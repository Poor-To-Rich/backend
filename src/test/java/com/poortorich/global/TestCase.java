package com.poortorich.global;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public interface TestCase<T, E> {

    static <T extends Enum<T> & TestCase<?, ?>> Stream<Arguments> getAllTestCases(Class<T> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).map(TestCase::getTestCase);
    }

    T getTestData();

    E getExpectedData();

    default Arguments getTestCase() {
        return Arguments.of(getTestData(), getExpectedData());
    }
}
