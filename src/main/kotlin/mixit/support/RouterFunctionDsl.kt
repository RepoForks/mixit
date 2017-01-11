package mixit.support

import org.springframework.core.io.Resource
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RouterFunctions.route
import reactor.core.publisher.Mono


fun RouterFunctionDsl(configure: RouterFunctionDsl.()->Unit) = RouterFunctionDsl().apply(configure)

class RouterFunctionDsl {

    val children = mutableListOf<RouterFunctionDsl>()
    val routes = mutableListOf<RouterFunction<ServerResponse>>()

    operator fun RequestPredicate.invoke(f: () -> HandlerFunction<ServerResponse>) {
        routes += route(this, f())
    }

    infix fun RequestPredicate.and(other: RequestPredicate): RequestPredicate = this.and(other)

    infix fun RequestPredicate.or(other: RequestPredicate): RequestPredicate = this.and(other)

    operator fun RequestPredicate.not(): RequestPredicate = this.negate()

    fun GET(pattern: String): RequestPredicate {
        return RequestPredicates.GET(pattern)
    }

    fun GET(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.GET(pattern), f())
    }

    fun HEAD(pattern: String): RequestPredicate {
        return RequestPredicates.HEAD(pattern)
    }

    fun HEAD(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.HEAD(pattern), f())
    }

    fun POST(pattern: String): RequestPredicate {
        return RequestPredicates.POST(pattern)
    }

    fun POST(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.POST(pattern), f())
    }

    fun PUT(pattern: String): RequestPredicate {
        return RequestPredicates.PUT(pattern)
    }

    fun PUT(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.PUT(pattern), f())
    }

    fun PATCH(pattern: String): RequestPredicate {
        return RequestPredicates.PATCH(pattern)
    }

    fun PATCH(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.PATCH(pattern), f())
    }

    fun DELETE(pattern: String): RequestPredicate {
        return RequestPredicates.DELETE(pattern)
    }

    fun DELETE(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.DELETE(pattern), f())
    }

    fun OPTIONS(pattern: String): RequestPredicate {
        return RequestPredicates.OPTIONS(pattern)
    }

    fun OPTIONS(pattern: String, f: () -> HandlerFunction<ServerResponse>) {
        routes += route(RequestPredicates.OPTIONS(pattern), f())
    }

    fun resources(path: String, location: Resource) {
        routes +=  RouterFunctions.resources(path, location)
    }

    @Suppress("UNCHECKED_CAST")
    fun router(): RouterFunction<ServerResponse> {
        return routes().reduce(RouterFunction<*>::and) as RouterFunction<ServerResponse>
    }

    operator fun invoke(request: ServerRequest): Mono<HandlerFunction<ServerResponse>> {
        return router().route(request)
    }

    private fun routes(): List<RouterFunction<ServerResponse>> {
        val allRoutes = mutableListOf<RouterFunction<ServerResponse>>()
        allRoutes += routes
        for (child in children) {
            allRoutes += child.routes()
        }
        return allRoutes
    }

}
