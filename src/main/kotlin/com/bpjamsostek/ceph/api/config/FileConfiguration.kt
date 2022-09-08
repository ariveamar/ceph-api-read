package com.bpjamsostek.ceph.api.config

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FileConfiguration {
    @Value("\${storage.url}")
    private val url: String? = null

    @Value("\${storage.accessKey}")
    private val accessKey: String? = null

    @Value("\${storage.secretKey}")
    private val secretKey: String? = null

    @Bean
    fun getCephStorageClient(): AmazonS3 {
        val basicAWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        val clientConfig = ClientConfiguration()
        clientConfig.protocol = Protocol.HTTP
        val endpointConfig = AwsClientBuilder.EndpointConfiguration(url, "")
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(basicAWSCredentials))
            .withClientConfiguration(clientConfig)
            .withEndpointConfiguration(endpointConfig)
            .build()
    }
}