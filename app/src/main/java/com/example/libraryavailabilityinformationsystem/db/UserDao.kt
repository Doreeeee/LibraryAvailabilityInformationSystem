package com.example.libraryavailabilityinformationsystem.db

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) OR LOWER(username) = LOWER(:username) LIMIT 1")
    fun getUserByEmailOrUsername(email: String, username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    fun deleteUser(userId: Int)
}
