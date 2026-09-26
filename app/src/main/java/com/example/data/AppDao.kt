package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Presentations
    @Query("SELECT * FROM presentations ORDER BY updatedAt DESC")
    fun getAllPresentations(): Flow<List<PresentationEntity>>

    @Query("SELECT * FROM presentations WHERE id = :id LIMIT 1")
    fun getPresentationById(id: String): Flow<PresentationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresentation(presentation: PresentationEntity)

    @Query("DELETE FROM presentations WHERE id = :id")
    suspend fun deletePresentationById(id: String)

    @Query("DELETE FROM presentations")
    suspend fun deleteAllPresentations()

    // Slides
    @Query("SELECT * FROM slides WHERE presentationId = :presentationId ORDER BY `index` ASC")
    fun getSlidesForPresentation(presentationId: String): Flow<List<SlideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlides(slides: List<SlideEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlide(slide: SlideEntity)

    @Update
    suspend fun updateSlide(slide: SlideEntity)

    @Query("DELETE FROM slides WHERE id = :id")
    suspend fun deleteSlideById(id: String)

    @Query("DELETE FROM slides WHERE presentationId = :presentationId")
    suspend fun deleteSlidesByPresentation(presentationId: String)

    // Practice Sessions
    @Query("SELECT * FROM practice_sessions WHERE presentationId = :presentationId ORDER BY timestamp DESC")
    fun getSessionsForPresentation(presentationId: String): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE id = :sessionId LIMIT 1")
    fun getSessionById(sessionId: String): Flow<PracticeSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeSession(session: PracticeSessionEntity)

    @Query("DELETE FROM practice_sessions WHERE presentationId = :presentationId")
    suspend fun deleteSessionsByPresentation(presentationId: String)

    @Query("DELETE FROM practice_sessions")
    suspend fun deleteAllSessions()

    // User Progress
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgressEntity)
}
