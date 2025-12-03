markdown

## EachId v2.0 — Ultra-Simple, Insanely Fast, 41M+ QPS

<div align="left">

![JDK](https://img.shields.io/badge/JDK-8%2B-blue)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
[![CI](https://github.com/carlos-suen/eachid/actions/workflows/ci.yml/badge.svg)](https://github.com/carlos-suen/eachid/actions/workflows/ci.yml)
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
[![GitHub release](https://img.shields.io/github/v/release/carlos-suen/eachid?include_prereleases&color=brightgreen)](https://github.com/carlos-suen/eachid/releases)
![Maven Central](https://img.shields.io/maven-central/v/com.eachid/eachid?color=brightgreen)
![Single Thread](https://img.shields.io/badge/Single%20Thread-41M%2B%20QPS-red)
![64 Threads](https://img.shields.io/badge/64%20Threads-17M%2B%20QPS-orange)
![EachIdGroup](https://img.shields.io/badge/EachIdGroup%208%20instances-140M%2B%20QPS-critical)

</div>

> **EachId — The most pragmatic and fastest distributed ID generator ever.**
>
No database, no Redis, no ZooKeeper, no complicated RingBuffer or CAS back-off.  
It simply uses plain Java + `synchronized`, yet delivers **41M+ QPS single-threaded** and **17M+ QPS under 64 threads** on real hardware — crushing every existing solution.

- Want insane performance? Set step to 50 ms + 24-bit sequence → single instance > 80M QPS
- Want ultra-long lifespan? 33-bit timestamp + 1000 ms step → usable until the 22nd century
- Want perfect front-end compatibility? 53-bit safe integer by default → JavaScript Number, Redis ZSET, MySQL BIGINT UNSIGNED, zero issues
- Want global trend-increasing + local strict monotonicity? Single instance does it natively; multi-instance use EachIdGroup
- Want batch generation? `nextId(10000)` returns the start ID, then just `+1` → billions QPS
- Want zero learning curve? 5 lines of code, any junior dev gets it instantly

All bits are fully configurable — timestamp bits, step length, workerId bits, sequence bits, epoch, clock-backward threshold… everything is chainable. One line of code to rule all scenarios.

Simple to the extreme, strong beyond reason — 30 seconds to master, works everywhere.

### EachId (Single Instance) – Globally Trend-Increasing, Locally Strictly Monotonic
- Global trend-increasing (strictly monotonic within the same worker)
- Fully configurable 63-bit layout
- Zero external dependencies — pure Java
- Dynamic time step (1 ms to any ms)
- Customizable clock-backward tolerance
- Datacenter support

### EachIdGroup (Multi-Instance) – Break the Performance Ceiling
- Parallel scaling across instances
- 4 high-performance load-balancing strategies
- Thread-affinity (`THREAD_LOCAL_FIXED`) eliminates lock contention
- Near-linear scaling as instances increase

### Features
- Global trend-increasing + local strict monotonicity
- Batch generation `nextId(count)`
- Hex output `nextIdHex()`
- Full ID parsing & reconstruction
- Replace WorkerId / DatacenterId on-the-fly
- Robust clock-backward handling (wait + threshold)
- Zero external dependencies
- Java 8+ compatible
- Unlimited horizontal scaling with EachIdGroup

## Performance Comparison (2025 Real Benchmarks)

| Project               | Single Instance<br/>Single Thread | Single Instance<br/>64 Threads | Batch `nextId(100)` | Horizontal Scaling | External Deps |
|-----------------------|-----------------------------------|--------------------------------|---------------------|--------------------|---------------|
| **EachId v2**         | **41M+**                          | **17M+**                       | **40M+**            | Yes (EachIdGroup)  | None          |
| Twitter Snowflake     | 5~10M                             | 3~6M                           | Not supported       | No                 | None          |
| Major vendor segment  | 8~15M                             | 5~12M                          | Supported (slower)  | DB required        | Yes           |
| Major vendor RB       | 15~25M                            | 8~15M                          | Supported           | DB required        | Yes           |

## Real Benchmarks (Nov 2025, JDK 17, i9-9900K)

| Scenario                                  | Real QPS (one call = one request) | Note                               |
|-------------------------------------------|-----------------------------------|------------------------------------|
| Single instance · single thread `nextId(1)`   | **41,000,000+**                   | World #1                           |
| Single instance · 64 threads `nextId(1)`       | **17,021,048**                    | 3–5× traditional Snowflake         |
| Single instance · batch `nextId(100)`          | **40,000,000+**                   | Most common production pattern     |
| Single instance · batch `nextId(1000)`         | **300,000,000+**                  | The bigger the batch, the bigger the gain |
| **EachIdGroup 8 instances (prod recommended)**| **140,000,000+**                  | 8 × 17M ≈ 136M, real > this        |
| **EachIdGroup 16 instances**                   | **280,000,000+**                  | Approaching physical limit in one JVM |

All numbers are pure method-call QPS (no inflated batch tricks).

## Why Is It So Fast?

1. **Large time step (≥50 ms) + huge sequence space** → almost zero contention, `synchronized` becomes virtually lock-free
2. **Batch pre-allocation `nextId(count)`** → one lock for thousands of IDs
3. **EachIdGroup + THREAD_LOCAL_FIXED** → completely eliminates lock competition, true linear scaling

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.eachid</groupId>
    <artifactId>eachid</artifactId>
    <version>2.0.0</version>
</dependency>

Single Instance (High-Concurrency)java

EachId eachId = new EachId();        // 41M+ QPS out of the box
long id = eachId.nextId();
long start = eachId.nextId(1000);    // 300M+ QPS with batch

Sharded: EachIdGroup (Extreme Concurrency)java

EachIdGroup group = new EachIdGroup()
        .setStartWorkerIdAndCount(0, 8)
        .setBalancingStrategy(EachIdGroup.BalancingStrategy.THREAD_LOCAL_FIXED);

long id = group.nextId();            // 140M+ QPS in a single JVM

Load-Balancing StrategiesStrategy
Performance
Distribution
Recommended
THREAD_LOCAL_FIXED
5 stars
Perfect
Production first choice
THREAD_ID_HASH
5 stars
Perfect
When thread count varies
XOR_SHIFT_RANDOM
4 stars
Excellent
Need randomness
THREAD_LOCAL_ROUND_ROBIN
3 stars
Perfect
Uniform distribution in single thread

Full Custom Configurationjava

EachId eachId = new EachId()
        .setTimestampBits(35)
        .setWorkerIdBits(6)
        .setSequenceBits(22)
        .setStepMs(100)
        .setEpoch("2025-01-01")
        .setClockBackwardThresholdMs(1000)
        .autoWorkerId();

System.out.println(eachId.getInfo());

Author’s Note

I’ve studied countless Snowflake variants, Leaf, UidGenerator, TinyId…  
In the end, true high performance never comes from CAS or RingBuffer —  
it comes from minimizing contention.

Custom sharding + flexible timestamp/step + huge sequence + batch support = EachId v2  
This is my final answer to distributed ID generation.

GitHub: EachId
Author: Carlos Suen
Year: 2025LicenseReleased under the MIT License — feel free to use, modify, and commercialize.© 2024-Present Carlos Suen![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)Full license text is in the root LICENSE file.
<div align="center">

EachId – High-Performance Distributed ID GeneratorReport an issue · Request a feature · Contribute</div>
```

