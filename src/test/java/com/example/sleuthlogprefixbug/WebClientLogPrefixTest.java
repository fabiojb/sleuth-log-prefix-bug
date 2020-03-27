package com.example.sleuthlogprefixbug;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.sleuth.instrument.web.client.TraceWebClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class WebClientLogPrefixTest {

    private static Logger logger = LoggerFactory.getLogger(WebClientLogPrefixTest.class);

    @Nested
    @SpringBootTest
    @Import(WebClientConfiguration.class)
    class WithSleuth {

        @Autowired
        private WebClient webClient;

        @Test
        void test() {
            var mono = webClient.post().uri("/status/400")
                    .retrieve()
                    .onStatus(httpStatus -> true, c -> Mono.just(c.logPrefix())
                            .doOnNext(logPrefix -> logger.info("{}<- logPrefix", logPrefix))
                            .map(LogPrefixContentException::new))
                    .toBodilessEntity();

            StepVerifier.create(mono)
                    .expectErrorSatisfies(t -> {
                        var e = (LogPrefixContentException)t;
                        assertFalse(e.getLogPrefix().isBlank());
                    })
                    .verify();
        }
    }

    @Nested
    @SpringBootTest
    @EnableAutoConfiguration(exclude = TraceWebClientAutoConfiguration.class)
    @Import(WebClientConfiguration.class)
    class WithoutSleuth {

        @Autowired
        private WebClient webClient;

        @Test
        void test() {
            var mono = webClient.post().uri("/status/400")
                    .retrieve()
                    .onStatus(httpStatus -> true, c -> Mono.just(c.logPrefix())
                            .doOnNext(logPrefix -> logger.info("{}<- logPrefix", logPrefix))
                            .map(LogPrefixContentException::new))
                    .toBodilessEntity();

            StepVerifier.create(mono)
                    .expectErrorSatisfies(t -> {
                        var e = (LogPrefixContentException)t;
                        assertFalse(e.getLogPrefix().isBlank());
                    })
                    .verify();
        }
    }
}

