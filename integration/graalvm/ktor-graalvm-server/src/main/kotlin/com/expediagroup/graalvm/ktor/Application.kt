/*
 * Copyright 2023 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graalvm.ktor

import com.expediagroup.graalvm.ktor.context.CustomContextFactory
import com.expediagroup.graalvm.ktor.hooks.CustomHooks
import com.expediagroup.graalvm.ktor.schema.ArgumentQuery
import com.expediagroup.graalvm.ktor.schema.AsyncQuery
import com.expediagroup.graalvm.ktor.schema.BasicMutation
import com.expediagroup.graalvm.ktor.schema.ContextualQuery
import com.expediagroup.graalvm.ktor.schema.CustomScalarQuery
import com.expediagroup.graalvm.ktor.schema.EnumQuery
import com.expediagroup.graalvm.ktor.schema.ErrorQuery
import com.expediagroup.graalvm.ktor.schema.IdQuery
import com.expediagroup.graalvm.ktor.schema.model.ExampleInterface
import com.expediagroup.graalvm.ktor.schema.model.ExampleUnion
import com.expediagroup.graalvm.ktor.schema.InnerClassQuery
import com.expediagroup.graalvm.ktor.schema.ListQuery
import com.expediagroup.graalvm.ktor.schema.PolymorphicQuery
import com.expediagroup.graalvm.ktor.schema.ScalarQuery
import com.expediagroup.graalvm.ktor.schema.TypesQuery
import com.expediagroup.graalvm.ktor.schema.dataloader.ExampleDataLoader
import com.expediagroup.graalvm.ktor.schema.model.FirstImpl
import com.expediagroup.graalvm.ktor.schema.model.FirstUnionMember
import com.expediagroup.graalvm.ktor.schema.model.SecondImpl
import com.expediagroup.graalvm.ktor.schema.model.SecondUnionMember
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        install(GraphQL) {
            schema {
                packages = listOf("com.expediagroup.graalvm.ktor")
                queries = listOf(
                    ArgumentQuery(),
                    AsyncQuery(),
                    ContextualQuery(),
                    CustomScalarQuery(),
                    EnumQuery(),
                    ErrorQuery(),
                    IdQuery(),
                    InnerClassQuery(),
                    ListQuery(),
                    PolymorphicQuery(),
                    ScalarQuery(),
                    TypesQuery()
                )
                mutations = listOf(
                    BasicMutation()
                )
                hooks = CustomHooks()
                typeHierarchy = mapOf(
                    ExampleInterface::class to listOf(FirstImpl::class, SecondImpl::class),
                    ExampleUnion::class to listOf(FirstUnionMember::class, SecondUnionMember::class)
                )
            }
            engine {
                dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory(
                    ExampleDataLoader
                )
            }
            server {
                contextFactory = CustomContextFactory()
            }
        }
        routing {
            graphQLGetRoute()
            graphQLPostRoute()
            graphQLSDLRoute()
            graphiQLRoute()
        }
    }.start(wait = true)
}
