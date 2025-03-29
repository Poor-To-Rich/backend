package com.poortorich.global.testcases;

import com.poortorich.global.TestCase;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class TestCaseArgumentsProvider<T extends Enum<T> & TestCase<?, ?>> implements ArgumentsProvider {
    private final Class<T> enumClass;

    public TestCaseArgumentsProvider(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return TestCase.getAllTestCases(enumClass);
    }
}
