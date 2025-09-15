package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Получить список пользователей")
    request {
        method 'GET'
        url '/api/account/users'
        headers {
            accept(applicationJson())
        }
    }
    response {
        status 200
        body([
                [
                        id: 1,
                        username: "user1",
                        password: "hashed_password_1",
                        personName: "Иван Иванов",
                        dateOfBirth: "1990-01-01"
                ],
                [
                        id: 2,
                        username: "user2",
                        password: "hashed_password_2",
                        personName: "Петр Петров",
                        dateOfBirth: "1985-05-15"
                ]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}