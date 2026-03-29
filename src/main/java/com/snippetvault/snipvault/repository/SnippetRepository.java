package com.snippetvault.snipvault.repository;

import com.snippetvault.snipvault.model.snippet;
import jdk.jshell.Snippet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository                 // marks this as spring data layer Bean (sql exception -> spring Exception)
public interface SnippetRepository extends JpaRepository<snippet, Long> {

    // At runtime generates code for findAll(),findById(),delete,update

    // search by language
    List<snippet> findByLanguageIgnoreCase(String language);

    //search by title containing a keyword
    List<snippet> findByTitleContainingIgnoreCase(String keyword);

    @Query("""
            SELECT s FROM snippet s
            WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<snippet> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
/*
@Query("...") — tells Spring: ignore the method name, run this JPQL instead. The string is the query.

"""...""" — Java 15+ text block. Lets you write multi-line strings cleanly without + concatenation.

LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) — case-insensitive contains check. CONCAT builds the %keyword% pattern. LOWER on both sides ensures case doesn't matter. This is the JPQL equivalent of SQL's ILIKE.

:keyword — a named parameter. Spring binds the value of the keyword argument to every :keyword placeholder in the query.

@Param("keyword") — links the Java method parameter to the :keyword name in the query. Without this, Spring can't match them up.

Page<Snippet> return type — passing Pageable as a second argument and returning Page makes this query paginated automatically. Spring runs the query twice under the hood: once for the data, once for the count (to compute totalPages).
 */
