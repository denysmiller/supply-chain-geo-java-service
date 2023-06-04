package org.ksl.supplychain.geography.resiliency;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.ksl.supplychain.common.infra.exception.CommunicationException;
import org.ksl.supplychain.common.infra.resiliency.ConfigurableExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.ecwid.consul.ConsulException;

import net.jodah.failsafe.TimeoutExceededException;

public class CustomFailsafeExecutorTest {

	ConfigurableExecutor executor;

	@BeforeEach
	void setup() {
		executor = new CustomFailsafeExecutor();
	}

	@Nested
	public class MockTests {
		@Test
		void execute_retryEnabled_success() {
			Config config = mockConfig();
			Supplier<String> supplier = () -> "ok";
			long currentTime = System.nanoTime();
			String result = executor.execute(config, supplier);
			long duration = (System.nanoTime() - currentTime) / 1_000_000;
			assertTrue(duration < 50);
			assertEquals("ok", result);
		}

		@Test
		void execute_retryEnabledWithFailures_success() {
			Config config = mockConfig();

			Supplier<String> supplier = () -> {
				throw new ConsulException("failure");
			};
			long currentTime = System.nanoTime();
			CommunicationException ex = assertThrows(CommunicationException.class,
					() -> executor.execute(config, supplier));
			assertEquals(ex.getMessage(), "failure");
			long duration = (System.nanoTime() - currentTime) / 1_000_000;
			assertTrue(duration > 350 && duration < 450);
		}

		private Config mockConfig() {
			Config config = Mockito.mock(Config.class);
			Properties properties = loadProperties("config-retry.properties");

			Answer<Optional<?>> answer = invocation -> {
				String property = invocation.getArgument(0, String.class);
				Class<?> clz = invocation.getArgument(1, Class.class);
				String value = (String) properties.get(property);
				if (clz == Long.class) {
					return Optional.ofNullable(Long.parseLong(value));
				} else if (clz == Double.class) {
					return Optional.ofNullable(Double.parseDouble(value));
				} else if (clz == Boolean.class) {
					return Optional.ofNullable(Boolean.parseBoolean(value));
				} else if (clz == Integer.class) {
					return Optional.ofNullable(Integer.parseInt(value));
				}
				return Optional.ofNullable(value);
			};

			Mockito.when(config.getOptionalValue(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class.class)))
					.thenAnswer(answer);
			return config;
		}
	}

	@Nested
	public class SystemPropertiesTests {

		@BeforeEach
		void setup() {
			Properties properties = loadProperties("config-retry.properties");
			for (Entry<Object, Object> entry : properties.entrySet()) {
				System.setProperty((String) entry.getKey(), (String) entry.getValue());
			}
		}

		@Test
		void execute_retryEnabledWithFailures_success() {
			Config config = ConfigProvider.getConfig();

			Supplier<String> supplier = () -> {
				throw new ConsulException("failure");
			};
			long currentTime = System.nanoTime();
			CommunicationException ex = assertThrows(CommunicationException.class,
					() -> executor.execute(config, supplier));
			assertEquals("failure", ex.getMessage());
			long duration = (System.nanoTime() - currentTime) / 1_000_000;
			assertTrue(duration > 350 && duration < 450);
		}
	}

	@Nested
	public class PropertyFilesTests {

		@Test
		void execute_timeoutEnabled_interrupted() {
			Config config = ConfigProvider.getConfig();

			Supplier<String> supplier = () -> {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				return "ok";
			};
			long currentTime = System.nanoTime();
			CommunicationException ex = assertThrows(CommunicationException.class,
					() -> executor.execute(config, supplier));
			assertTrue(ex.getCause() instanceof TimeoutExceededException);
			TimeoutExceededException ex2 = (TimeoutExceededException) ex.getCause();
			assertEquals(Duration.ofMillis(300), ex2.getTimeout().getTimeout());
			long duration = (System.nanoTime() - currentTime) / 1_000_000;
			assertTrue(duration > 300 && duration < 500);
		}

		@Test
		@Disabled
		void execute_timeoutDisabled_normalExecution() {
			System.setProperty("smallrye.config.profile", "noop");
			try {
				Config config = ConfigProviderResolver.instance().getBuilder().addDefaultSources().build();

				Supplier<String> supplier = () -> {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					return "ok";
				};
				long currentTime = System.nanoTime();
				assertEquals("ok", executor.execute(config, supplier));
				long duration = (System.nanoTime() - currentTime) / 1_000_000;
				assertTrue(duration > 500);
			} finally {
				System.getProperties().remove("smallrye.config.profile");
			}
		}

	}

	private Properties loadProperties(String file) {
		try (InputStream in = CustomFailsafeExecutorTest.class.getClassLoader()
				.getResourceAsStream("configuration/" + file)) {
			Properties props = new Properties();
			props.load(in);
			return props;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
