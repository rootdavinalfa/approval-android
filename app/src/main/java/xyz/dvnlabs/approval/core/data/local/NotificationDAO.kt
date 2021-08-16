/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(notification: Notification)

    @Query("SELECT * FROM Notification WHERE receivedUser = :user")
    suspend fun getAllByUser(user: String): List<Notification>

    @Query(
        "UPDATE Notification SET flag = '1' WHERE receivedUser = :user AND idNotification = :idNotification"
    )
    suspend fun notificationRead(user: String, idNotification: String)

    @Query("SELECT EXISTS(SELECT * FROM Notification WHERE receivedUser = :user AND idNotification = :idNotification)")
    suspend fun isExist(user: String, idNotification: String): Boolean
}