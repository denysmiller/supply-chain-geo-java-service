package org.ksl.supplychain.geography.resiliency;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.eclipse.microprofile.config.Config;
import org.ksl.supplychain.common.infra.exception.CommunicationException;
import org.ksl.supplychain.common.infra.resiliency.ConfigurableExecutor;

import com.ecwid.consul.ConsulException;

import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.Timeout;

@Slf4j
public class CustomFailsafeExecutor implements ConfigurableExecutor {
	private static final String CONFIG_BASE = "supplychain.consul.";

	private static final String CONFIG_BASE_RETRY = CONFIG_BASE + "retry.";

	private static final String CONFIG_BASE_TIMEOUT = CONFIG_BASE + "timeout.";

	private static final String CONFIG_RETRY_ATTEMPTS = CONFIG_BASE_RETRY + "attempts";

	private static final String CONFIG_RETRY_ENABLED = CONFIG_BASE_RETRY + "enabled";

	private static final String CONFIG_RETRY_INTERVAL = CONFIG_BASE_RETRY + "interval";

	private static final String CONFIG_RETRY_MULTIPLIER = CONFIG_BASE_RETRY + "multiplier";

	private static final String CONFIG_TIMEOUT_INTERVAL = CONFIG_BASE_TIMEOUT + "interval";

	private static final String CONFIG_TIMEOUT_ENABLED = CONFIG_BASE_TIMEOUT + "enabled";

	/**
	 * Default time-out in milliseconds
	 */
	private static final int DEFAULT_TIMEOUT = 10_000;

	private static final double DEFAULT_DELAY_FACTOR = 2;

	private static final long DEFAULT_RETRY_INTERVAL = 2000;
	
	public CustomFailsafeExecutor() {
		log.info("Starting CustomFailsafeExecutor");
	}

	private <T> RetryPolicy<T> createRetryPolicy(Config config) {
		RetryPolicy<T> retryPolicy = new RetryPolicy<>();
		retryPolicy.handle(ConsulException.class, TimeoutException.class);
		int maxRetries = config.getOptionalValue(CONFIG_RETRY_ATTEMPTS, Integer.class)
				.orElse(retryPolicy.getMaxAttempts());
		retryPolicy.withMaxRetries(maxRetries);

		double multiplier = config.getOptionalValue(CONFIG_RETRY_MULTIPLIER, Double.class).orElse(DEFAULT_DELAY_FACTOR);
		long delay = config.getOptionalValue(CONFIG_RETRY_INTERVAL, Long.class).orElse(DEFAULT_RETRY_INTERVAL);
		long maxDelay = (long) (delay * Math.pow(2, maxRetries));

		retryPolicy.withBackoff(delay, maxDelay, ChronoUnit.MILLIS, multiplier);

		retryPolicy.onAbort(event -> log.debug("Abort retry with error {} with retries #: {}",
				event.getFailure().getMessage(), event.getAttemptCount()));
		retryPolicy.onRetry(event -> log.debug("Retry error {}, retry attempt #: {}",
				event.getLastFailure().getMessage(), event.getAttemptCount()));
		retryPolicy.onSuccess(event -> log.debug("Retry succcess with duration {} and retries #: {}",
				event.getElapsedTime(), event.getAttemptCount()));
		return retryPolicy;
	}

	private <T> Timeout<T> createTimeoutPolicy(Config config) {
		Timeout<T> timeout = Timeout.of(Duration
				.ofMillis(config.getOptionalValue(CONFIG_TIMEOUT_INTERVAL, Integer.class).orElse(DEFAULT_TIMEOUT)));
		timeout = timeout.withCancel(true);
		timeout.onFailure(event -> log.debug(event.getFailure().getMessage(), event.getFailure()));

		return timeout;
	}

	@Override
	public <T> T execute(Config config, Supplier<T> supplier) {
		boolean retryEnabled = config.getOptionalValue(CONFIG_RETRY_ENABLED, Boolean.class).orElse(true);

		boolean timeoutEnabled = config.getOptionalValue(CONFIG_TIMEOUT_ENABLED, Boolean.class).orElse(true);

		try {
			List<Policy<T>> policies = new ArrayList<>();
			if (retryEnabled) {
				policies.add(createRetryPolicy(config));
			}
			if (timeoutEnabled) {
				policies.add(createTimeoutPolicy(config));
			}
			if (policies.isEmpty()) {
				return supplier.get();
			}
			FailsafeExecutor<T> executor = Failsafe.with(policies);
			return executor.get(() -> supplier.get());

		} catch (Throwable e) {
			throw new CommunicationException(e.getMessage(), e);
		}
	}
}
