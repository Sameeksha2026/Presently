package com.example.data

import com.example.model.PracticeSession
import com.example.model.Presentation
import com.example.model.Slide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PresentationRepository(private val dao: AppDao) {

    val allPresentations: Flow<List<Presentation>> = dao.getAllPresentations()
        .map { entityList ->
            entityList.map { entity ->
                DataMappers.toPresentationDomain(entity, emptyList())
            }
        }

    fun getPresentationWithSlides(presentationId: String): Flow<Presentation?> {
        return combine(
            dao.getPresentationById(presentationId),
            dao.getSlidesForPresentation(presentationId)
        ) { presEntity, slideEntities ->
            if (presEntity == null) null
            else {
                val slides = slideEntities.map { DataMappers.toSlideDomain(it) }
                DataMappers.toPresentationDomain(presEntity, slides)
            }
        }
    }

    suspend fun savePresentation(presentation: Presentation) {
        val presEntity = DataMappers.toPresentationEntity(presentation)
        dao.insertPresentation(presEntity)
        val slideEntities = presentation.slides.map {
            DataMappers.toSlideEntity(it, presentation.id)
        }
        dao.deleteSlidesByPresentation(presentation.id)
        dao.insertSlides(slideEntities)
    }

    suspend fun updateSlide(slide: Slide, presentationId: String) {
        val entity = DataMappers.toSlideEntity(slide, presentationId)
        dao.insertSlide(entity)
    }

    suspend fun deleteSlide(slideId: String) {
        dao.deleteSlideById(slideId)
    }

    suspend fun deletePresentation(id: String) {
        dao.deleteSlidesByPresentation(id)
        dao.deleteSessionsByPresentation(id)
        dao.deletePresentationById(id)
    }

    suspend fun deleteAllPresentations() {
        dao.deleteAllPresentations()
    }

    suspend fun savePracticeSession(session: PracticeSession) {
        val entity = DataMappers.toPracticeSessionEntity(session)
        dao.insertPracticeSession(entity)
    }

    fun getSessionsForPresentation(presentationId: String): Flow<List<PracticeSession>> {
        return dao.getSessionsForPresentation(presentationId).map { list ->
            list.map { DataMappers.toPracticeSessionDomain(it) }
        }
    }

    val allSessions: Flow<List<PracticeSession>> = dao.getAllSessions().map { list ->
        list.map { DataMappers.toPracticeSessionDomain(it) }
    }

    suspend fun deleteAllSessions() {
        dao.deleteAllSessions()
    }

    val userProgress: Flow<UserProgressEntity?> = dao.getUserProgress()

    suspend fun updateUserProgress(progress: UserProgressEntity) {
        dao.insertOrUpdateProgress(progress)
    }
}
