package ricardo.fellini.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Locale;

@Slf4j
public class MonoTest {
    /**
     * Reactive Streams
     * 1. Asynchronous
     * 2. Non-blocking
     * 3. Backpressure
     *  Publisher (subscribe) Subscriber
     *  Subscription is created
     *  Publisher (onSubscribe with the subscription) -> Subscriber
     *  Subscription <- (request N) Subscriber
     *  Publisher -> (onNext) Subscriber
     *  until:
     *  1. Publisher sends all the objects requested.
     *  2. Publisher sends all the objects is has. (onComplete) subscriber and subscription will be canceled.
     *  3. There is an error, (onError) -> subscriber and subscription will be canceled.
     */
    @Test
    public void monoSubscriber(){

        String name = "Ricardo";

        Mono<String> mono = Mono.just(name)
                .log();

        mono.subscribe();

        StepVerifier.create(mono)
                        .expectNext(name)
                        .verifyComplete();

        log.info("Mono {}", mono);
        log.info("Working as intended.");
    }

    @Test
    public void subscriberConsumer(){

        String name = "Ricardo";

        Mono<String> mono = Mono.just(name)
                .log();

        mono.subscribe(s -> log.info("Value {}", s));

        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();

        log.info("Mono {}", mono);
        log.info("Working as intended.");
    }

    @Test
    public void monoSubscriberConsumerError(){

        String name = "Ricardo";

        Mono<String> mono = Mono.just(name)
                        .map(s -> {throw new RuntimeException("Testing mono with error");});

        mono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened."));
        mono.subscribe(s -> log.info("Name {}", s), Throwable::printStackTrace);

        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    public void subscriberConsumerComplete(){

        String name = "Ricardo";

        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value {}", s), Throwable::printStackTrace,
                () -> log.info("FINISHED!") );

        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();

    }

    @Test
    public void subscriberConsumerSubscription(){

        String name = "Ricardo";

        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value {}", s), Throwable::printStackTrace,
                () -> log.info("FINISHED!"), Subscription::cancel);

        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();

    }

    @Test
    public void monoDoOnMethods(){

        String name = "Ricardo";

        Mono<Object> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(logNumber -> log.info("Request received, start doing something."))
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .doOnSuccess(s-> log.info("doOnSuccess executed. {}", s));

        mono.subscribe(s -> log.info("Value {}", s), Throwable::printStackTrace,
                () -> log.info("FINISHED!"), Subscription::cancel);

        StepVerifier.create(mono)
               // .expectNext(name.toUpperCase())
                .verifyComplete();

    }


}
