package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Отправить уведомление")
    request {
        method 'POST'
        url '/api/notifications'
        headers {
            contentType(applicationJson())
        }
        body(
                login: 'user_login',
                message: 'test message'
        )
    }
    response {
        status 200
    }
}