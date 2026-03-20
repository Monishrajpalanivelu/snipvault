package com.snippetvault.snipvault.repository;

import com.snippetvault.snipvault.model.snippet;
import jdk.jshell.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository                 // marks this as spring data layer Bean (sql exception -> spring Exception)
public interface SnippetRepository extends JpaRepository<snippet, Long> {

    // At runtime generates code for findAll(),findById(),delete,update

    // search by language
    List<snippet> findByLanguageIgnoreCase(String language);

    //search by title containing a keyword
    List<snippet> findByTitleContainingIgnoreCase(String keyword);
}
