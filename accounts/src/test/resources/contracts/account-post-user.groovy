package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Создать пользователя")
    request {
        method 'POST'
        url '/api/account/user'
        headers {
            contentType(applicationJson())
        }
        body(
                username: "newuser",
                password: "newpassword",
                personName: "Новый Пользователь",
                dateOfBirth: "2000-12-31"
        )
    }
    response {
        status 200
        body(
                id: 10,
                username: "newuser",
                password: "hashed_newpassword",
                personName: "Новый Пользователь",
                dateOfBirth: "2000-12-31"
        )
        headers {
            contentType(applicationJson())
        }
    }
}