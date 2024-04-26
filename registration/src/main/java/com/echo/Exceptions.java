package com.echo;

class AccessViolationException extends Exception {
    public AccessViolationException(String message) {
        super(message);
    }
}

class ExpiredSessionException extends Exception {
    public ExpiredSessionException(String message) {
        super(message);
    }
}

class Exceptions extends Exception {
    public Exceptions(String message) {
        super(message);
    }
}

class NotStudentException extends Exception {
    public NotStudentException(String message) {
        super(message);
    }
}

class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}

class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

class DuplicateRecordException extends Exception {
    public DuplicateRecordException(String message) {
        super(message);
    }
}