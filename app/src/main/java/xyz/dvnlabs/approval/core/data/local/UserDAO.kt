/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.local

import androidx.room.*

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: User)

    @Query("SELECT * FROM User WHERE userName = :userName")
    suspend fun getUser(userName: String): User?

    @Query("SELECT * FROM User")
    suspend fun getAllUser(): List<User>?

    @Delete
    suspend fun deleteUser(user: User)

}