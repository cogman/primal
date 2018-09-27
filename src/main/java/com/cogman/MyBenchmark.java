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

package com.cogman;

import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyBenchmark {
    @Benchmark
    public void primes(){
        long lastCheck = 2;
        List<Long> primes = new ArrayList<>();
        List<CompletableFuture<PrimeResult>> futures = new ArrayList<>(100);
        while (primes.size() < 10000)
        {
            for (long i = lastCheck; i < lastCheck + 1000; ++i)
            {
                long j = i;
                futures.add(CompletableFuture.supplyAsync(()->new PrimeResult(j, isPrime(j))));
            }
            lastCheck += 1000;
            futures.stream()
                    .map(CompletableFuture::join)
                    .filter((r)->r.isPrime)
                    .map((r)->r.primeValue)
                    .forEach(primes::add);
            futures.clear();
        }
    }

    public boolean isPrime(long potentialPrimes) {
        if (potentialPrimes < 2)
            return false;
        if (potentialPrimes == 2)
            return true;
        if (potentialPrimes % 2 == 0)
            return false;
        for (var i = 3; i <= Math.sqrt(potentialPrimes); i += 2) {
            if (potentialPrimes % i == 0)
                return false;
        }
        return true;
    }

    private static class PrimeResult
    {
        private final long primeValue;
        private final boolean isPrime;

        public PrimeResult(long primeValue, boolean isPrime) {
            this.primeValue = primeValue;
            this.isPrime = isPrime;
        }
    }
}
