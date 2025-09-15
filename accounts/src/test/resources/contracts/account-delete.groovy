package contracts

import org.springframework.lang.Contract

Contract.make {
    description("Удалить аккаунт по ID")
    request {
        method 'DELETE'
        url '/api/account/101'
    }
    response {
        status 204
    }
}

