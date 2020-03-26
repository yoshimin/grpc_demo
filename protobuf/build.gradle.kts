import com.google.protobuf.gradle.*

plugins {
    id("com.google.protobuf") version "0.8.11"
}

sourceSets{
    main {
        java {
            srcDir("build/generated/source/proto/main/grpc")
            srcDir("build/generated/source/proto/main/java")
            srcDir("build/generated/source/proto/main/reactorGrpc")
        }
    }
}

dependencies {
    api("io.github.lognet:grpc-spring-boot-starter:3.5.1")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.0.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
        id("reactorGrpc") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.0.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("reactorGrpc")
            }
            it.doFirst {
                delete("build/generated/source/proto")
            }
        }
    }
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
