package com.es.sessionsecurity.error.exception

class ForbiddenException(message: String) : RuntimeException("Forbidden Exception (403). $message") {
}