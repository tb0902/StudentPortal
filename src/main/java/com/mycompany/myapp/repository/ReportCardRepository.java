package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ReportCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReportCard entity.
 */
@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {
    @Query("select reportCard from ReportCard reportCard where reportCard.student.login = ?#{authentication.name}")
    List<ReportCard> findByStudentIsCurrentUser();

    @Query("select reportCard from ReportCard reportCard where reportCard.teacher.login = ?#{authentication.name}")
    List<ReportCard> findByTeacherIsCurrentUser();

    default Optional<ReportCard> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReportCard> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReportCard> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reportCard from ReportCard reportCard left join fetch reportCard.student left join fetch reportCard.teacher",
        countQuery = "select count(reportCard) from ReportCard reportCard"
    )
    Page<ReportCard> findAllWithToOneRelationships(Pageable pageable);

    @Query("select reportCard from ReportCard reportCard left join fetch reportCard.student left join fetch reportCard.teacher")
    List<ReportCard> findAllWithToOneRelationships();

    @Query(
        "select reportCard from ReportCard reportCard left join fetch reportCard.student left join fetch reportCard.teacher where reportCard.id =:id"
    )
    Optional<ReportCard> findOneWithToOneRelationships(@Param("id") Long id);
}
