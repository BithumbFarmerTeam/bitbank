package click.bitbank.api.infrastructure.config;

import click.bitbank.api.application.response.MemberLoginResponse;
import click.bitbank.api.application.response.MemberSignupResponse;
import click.bitbank.api.presentation.member.MemberHandler;
import click.bitbank.api.presentation.member.request.MemberSignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import click.bitbank.api.application.response.MemberInfoResponse;
import click.bitbank.api.presentation.member.request.MemberLoginRequest;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@EnableWebFlux // WebFlux 설정 활성화
public class WebFluxRouterConfig implements WebFluxConfigurer {

    @RouterOperations({
        @RouterOperation(
            path = "/auth/signup",
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE },
            beanClass = MemberHandler.class,
            method = RequestMethod.POST,
            beanMethod = "memberRegistration",
            operation = @Operation(
                description = "일반 회원 가입 API",
                operationId = "signup",
                requestBody = @RequestBody(
                    content = @Content(
                        schema = @Schema(
                            implementation = MemberSignupRequest.class,
                            required = true
                        )
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        content = @Content(
                            schema = @Schema(
                                implementation = MemberSignupResponse.class,
                                required = true
                            )
                        )
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/member/login",
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE },
            beanClass = MemberHandler.class,
            method = RequestMethod.POST,
            beanMethod = "login",
            operation = @Operation(
                description = "로그인 API",
                operationId = "login",
                requestBody = @RequestBody(
                    content = @Content(
                        schema = @Schema(
                            implementation = MemberLoginRequest.class,
                            required = true
                        )
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                            schema = @Schema(
                                implementation = MemberLoginResponse.class,
                                required = true
                            )
                        )
                    )
                }
            )
        )
    })
    @Bean
    public RouterFunction<ServerResponse> memberRouterBuilder(MemberHandler memberHandler) {

        return RouterFunctions.route()
            .path("/auth", memberBuilder ->
                memberBuilder.nest(accept(MediaType.APPLICATION_JSON), builder ->
                    builder
                        .POST("/signup", memberHandler::signup) // 학생 회원 가입
                        .POST("/login", memberHandler::login) // 로그인
                )
            )
            .build();
    }

    @RouterOperations({
        @RouterOperation(
            path = "/member/findMemberInfo/${memberId}",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            headers = { HttpHeaders.AUTHORIZATION },
            beanClass = MemberHandler.class,
            method = RequestMethod.GET,
            beanMethod = "findMemberInfo",
            operation = @Operation(
                description = "회원 정보 조회 API",
                operationId = "findMemberInfo",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                            schema = @Schema(
                                implementation = MemberInfoResponse.class,
                                required = true
                            )
                        )
                    )
                }
            )
        )
    })
    @Bean
    public RouterFunction<ServerResponse> memberRouterGETBuilder(MemberHandler memberHandler) {
        return RouterFunctions.route()
            .path("/member", builder -> builder
                .GET("/findMemberInfo/{memberId}", memberHandler::findMemberInfo)
            ).build();
    }

}
