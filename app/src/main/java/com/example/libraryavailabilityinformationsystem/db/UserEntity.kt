package com.example.libraryavailabilityinformationsystem.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryavailabilityinformationsystem.models.UserAccount
import com.example.libraryavailabilityinformationsystem.models.UserRole
import com.example.libraryavailabilityinformationsystem.util.PasswordHasher

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val username: String,
    val passwordHash: String = "",
    val role: String,
    val studentId: String? = null,
    val department: String? = "Computer Science",
    val phone: String? = "+1 (555) 019-2834",
    val isActive: Boolean = true,
    val createdAt: String = "15 Jan 2025",
    val uid: String? = null,
    val syncStatus: String = "SYNCED"
)

fun UserEntity.toModel(): UserAccount {
    val roleEnum = when (role.uppercase()) {
        "LIBRARIAN", "LIBRARIAN STAFF" -> UserRole.Librarian
        "ADMIN", "ADMINISTRATOR" -> UserRole.Admin
        else -> UserRole.Student
    }
    return UserAccount(
        id = id,
        name = name,
        email = email,
        username = username,
        password = passwordHash,
        role = roleEnum,
        studentId = studentId,
        department = department,
        phone = phone,
        isActive = isActive,
        createdAt = createdAt,
        uid = uid
    )
}

fun UserAccount.toEntity(syncStatus: String = "SYNCED"): UserEntity {
    val pwdHash = if (password.length == 64 && password.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }) {
        password
    } else {
        PasswordHasher.hashPassword(password)
    }
    return UserEntity(
        id = id,
        name = name,
        email = email,
        username = username,
        passwordHash = pwdHash,
        role = role.name,
        studentId = studentId,
        department = department,
        phone = phone,
        isActive = isActive,
        createdAt = createdAt,
        uid = uid,
        syncStatus = syncStatus
    )
}
