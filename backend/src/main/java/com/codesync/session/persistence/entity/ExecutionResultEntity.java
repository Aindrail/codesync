package com.codesync.session.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "execution_result")
public class ExecutionResultEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verdict", nullable = false)
    private String verdict;

    @Column(name = "runtime_in_millis")
    private Integer runtimeInMillis;

    @Column(name = "runtime_percentile")
    private Double runtimePercentile;

    @Column(name = "memory_in_kb")
    private Integer memoryInKb;

    @Column(name = "memory_percentile")
    private Double memoryPercentile;

    @Column(name = "total_test_cases")
    private Integer totalTestCases;

    @Column(name = "passed_test_cases")
    private Integer passedTestCases;

    protected ExecutionResultEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public Integer getRuntimeInMillis() {
        return runtimeInMillis;
    }

    public void setRuntimeInMillis(Integer runtimeInMillis) {
        this.runtimeInMillis = runtimeInMillis;
    }

    public Double getRuntimePercentile() {
        return runtimePercentile;
    }

    public void setRuntimePercentile(Double runtimePercentile) {
        this.runtimePercentile = runtimePercentile;
    }

    public Integer getMemoryInKb() {
        return memoryInKb;
    }

    public void setMemoryInKb(Integer memoryInKb) {
        this.memoryInKb = memoryInKb;
    }

    public Double getMemoryPercentile() {
        return memoryPercentile;
    }

    public void setMemoryPercentile(Double memoryPercentile) {
        this.memoryPercentile = memoryPercentile;
    }

    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(Integer totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public Integer getPassedTestCases() {
        return passedTestCases;
    }

    public void setPassedTestCases(Integer passedTestCases) {
        this.passedTestCases = passedTestCases;
    }
}