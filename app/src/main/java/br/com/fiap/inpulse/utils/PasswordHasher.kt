package br.com.fiap.inpulse.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}