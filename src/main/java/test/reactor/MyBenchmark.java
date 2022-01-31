/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package test.reactor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Fork(value = 1, jvmArgsPrepend = {"-Xmx1g", "-Xms1g"})
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 1, time = 2)
public class MyBenchmark {
    private final static int LOOP = 100000;

    @Benchmark
    public void testDefaultParallelScheduler() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Flux.range(0, LOOP)
                .timeout(Duration.ofMillis(rnd.nextInt(100, 10000)), Mono.just(-1))
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(it -> !it.get(0).equals(-1))
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }

    @Benchmark
    public void testSingleScheduler() throws Exception {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Flux.range(0, LOOP)
                .timeout(Duration.ofMillis(rnd.nextInt(100, 10000)), Mono.just(-1), Schedulers.single())
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(it -> !it.get(0).equals(-1))
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }

}
