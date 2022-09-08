package com.bpjamsostek.ceph.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CephStorageReadApiApplication

fun main(args: Array<String>) {
	runApplication<CephStorageReadApiApplication>(*args)
}
