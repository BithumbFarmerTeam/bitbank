package click.bitbank.api.infrastructure.jwt;

import click.bitbank.api.infrastructure.exception.status.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import click.bitbank.api.infrastructure.filter.MemberType;

@SpringBootTest
public class JwtVerifyTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void FailParseToken() {
        String token = "aaaasdddibbJ2IUzI1NiJ9.eyJJRCI6MSwiQVVUSCI6IkFETUlOIiwiZXhwIjoxNjUwOTczODYzfQ.rKG30lEnAKj7ss0vB5MrAg3Es-DFsq6Dg4NN32iAbWg";

        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> jwtProvider.validateToken(token));
        Assertions.assertEquals("401 UNAUTHORIZED \"토큰이 올바르게 구성되지 않았습니다.\"", exception.getMessage());

    }
}
