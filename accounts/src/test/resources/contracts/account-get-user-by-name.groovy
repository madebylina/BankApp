package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Получить пользователя по имени username")
    request {
        method 'GET'
        urlPath('/api/account/user') {
            queryParameters {
                parameter 'username': 'user1'
            }
        }
        headers {
            accept(applicationJson())
        }
    }
    response {
        status 200
        body(
                id: 1,
                username: "user1",
                password: "hashed_password_1",
                personName: "Иван Иванов",
                dateOfBirth: "1990-01-01"
        )
        headers {
            contentType(applicationJson())
        }
    }
}