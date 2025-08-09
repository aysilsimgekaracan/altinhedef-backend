package net.altinhedef.altinhedef.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date

@Service
class JwtService {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val EXPIRATION_DURATION = 1000 * 60 * 60 * 10 // 10 hours

    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, Any> = HashMap()
        val roles = userDetails.authorities.map { it.authority }

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_DURATION))
            .signWith(secretKey)
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    fun extractUsername(token: String): String {
        val claims = extractAllClaims(token)
        return claims.subject
    }

    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = extractExpiration(token)
        return expirationDate.before(Date())
    }

    private fun extractExpiration(token: String): Date {
        val claims = extractAllClaims(token)
        return claims.expiration
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}