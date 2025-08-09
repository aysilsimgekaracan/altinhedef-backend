package com.altinhedef

import com.altinhedef.entity.Role
import com.altinhedef.entity.User
import com.altinhedef.repository.RoleRepository
import com.altinhedef.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String?) {
       logger.info("DataInitializer: Checking database default data")

       if (roleRepository.count() == 0L) {
           logger.info("DataInitializer: Could not found default roles, they are being created...")

           val adminRole = Role(name = "ADMIN")
           val studentRole = Role(name = "STUDENT")
           val teacherRole = Role(name = "TEACHER")

           roleRepository.saveAll(listOf(adminRole, studentRole, teacherRole))

           logger.info("DataInitializer: Created default roles successfully.")
       } else {
           logger.info("DataInitializer: Default roles are already created.")
       }

       if (!userRepository.existsByRole_Name("ADMIN")) {
           logger.info("DataInitializer: Creating first admin users")
           val adminRole = roleRepository.findByName("ADMIN")!!

           val adminUser = User(
               name = "admin",
               surname = "user",
               email = "test@aysilsimge.dev",
               passwordHash = passwordEncoder.encode("admin"),
               role = adminRole
           )
           userRepository.save(adminUser)
           logger.info("DataInitializer: Created first admin user")
       }
    }
}