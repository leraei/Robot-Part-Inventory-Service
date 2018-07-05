package com.kahoot.robotpartservice.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Parameter;

import static org.mockito.Mockito.mock;

/**
 * This class is a proof-of-concept version for mockito with Junit5 made by mockito. We have to use it unless mockito or junit offer a official extension.
 * See {@linktourl https://github.com/mockito/mockito/blob/release/2.x/subprojects/junit-jupiter/src/main/java/org/mockito/junit/jupiter/MockitoExtension.java}
 */
public class MockitoExtension implements TestInstancePostProcessor, ParameterResolver {

	@Override
	public void postProcessTestInstance(final Object testInstance,
										final ExtensionContext context) {
		MockitoAnnotations.initMocks(testInstance);
	}

	@Override
	public boolean supportsParameter(final ParameterContext parameterContext,
									 final ExtensionContext extensionContext) {
		return
			parameterContext.getParameter().isAnnotationPresent(Mock.class);
	}

	@Override
	public Object resolveParameter(final ParameterContext parameterContext,
								   final ExtensionContext extensionContext) {
		return getMock(parameterContext.getParameter(), extensionContext);
	}

	private Object getMock(
		final Parameter parameter, final ExtensionContext extensionContext) {

		final Class<?> mockType = parameter.getType();
		final ExtensionContext.Store mocks = extensionContext.getStore(ExtensionContext.Namespace.create(
			MockitoExtension.class, mockType));
		final String mockName = getMockName(parameter);

		if (mockName != null) {
			return mocks.getOrComputeIfAbsent(
				mockName, key -> mock(mockType, mockName));
		} else {
			return mocks.getOrComputeIfAbsent(
				mockType.getCanonicalName(), key -> mock(mockType));
		}
	}

	private String getMockName(final Parameter parameter) {
		final String explicitMockName = parameter.getAnnotation(Mock.class)
			.name().trim();
		if (!explicitMockName.isEmpty()) {
			return explicitMockName;
		} else if (parameter.isNamePresent()) {
			return parameter.getName();
		}
		return null;
	}
}
