package isel.daw.battleships

import isel.daw.battleships.database.jdbi.configure
import isel.daw.battleships.http.pipeline.AuthenticationInterceptor
import isel.daw.battleships.http.pipeline.UserArgumentResolver
import isel.daw.battleships.model.Author
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val VERSION = "0.01"

//const val ADDRESS = "localhost"
//const val POSTGRES_DB = "postgres"
//const val DB_USER = "postgres"
//const val DB_PASSWORD = "postgres"

val POSTGRES_DB = System.getenv("POSTGRES_DB")
val DB_USER = System.getenv("POSTGRES_USER")
val DB_PASSWORD = System.getenv("POSTGRES_PASSWORD")
val JDBC_DATABASE_URL = System.getenv("JDBC_DATABASE_URL")

const val TAG = "BattleshipsApplication"

val AUTHORS = listOf(
    Author("Miguel Almeida", 47249),
    Author("Ricardo Bernardino", 47283),
    Author("David Costa", 45935)
)

@SpringBootApplication
class BattleshipsApplication {

//    @Bean
//    fun jdbi() = Jdbi.create(
//        PGSimpleDataSource().apply {
//            setURL("jdbc:postgresql://$ADDRESS/$POSTGRES_DB?user=$DB_USER&password=$DB_PASSWORD")
//        }
//    ).configure()

    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(JDBC_DATABASE_URL)
        }
    ).configure()

}

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val userArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(authenticationInterceptor)
            .addPathPatterns("/game/**")
            .addPathPatterns("/me/**")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<BattleshipsApplication>(*args)
}

