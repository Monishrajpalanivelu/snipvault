package com.snippetvault.snipvault.Controller;

import com.snippetvault.snipvault.DTO.SnippetRequest;
import com.snippetvault.snipvault.DTO.SnippetResponse;
import com.snippetvault.snipvault.Service.SnippetService;
import com.snippetvault.snipvault.repository.SnippetRepository;
import jakarta.validation.Valid;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // has @Controller -> handles requests and @ResponseBody -> auto converts return values to JSON using jakson
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
public class SnippetController {

    private final SnippetService snippetService;


    // getallsnippets
    @GetMapping
    public ResponseEntity<Page<SnippetResponse>> getAllSnippets(@PageableDefault(
            size = 2,
            sort = "createdAt",
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        return ResponseEntity.ok(snippetService.getAllSnippets(pageable));
    }

    // get snippet by id
    @GetMapping("/{id}")
    public ResponseEntity<SnippetResponse> getSnippetById(@PathVariable Long id) {
        return ResponseEntity.ok(snippetService.getSnippetById(id));
    }

    // create new snippet
    @PostMapping
    public ResponseEntity<SnippetResponse> createSnippet(@Valid @RequestBody SnippetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(snippetService.createSnippet(request));

    }

    //update a snippet
    //@PathVariable translate the String id in the url to long id/datatype any id
    //@requestbody Tells Spring to read the JSON from the request body and deserialise(json ->java objects) it into a SnippetRequest object automatically.
    @PutMapping("{id}")
    public ResponseEntity<SnippetResponse> updateSnippet(@PathVariable Long id, @Valid @RequestBody SnippetRequest request) {

        return ResponseEntity.ok(snippetService.updateSnippet(id, request));

    }

    // delete snippet
    @DeleteMapping("{id}")
    public ResponseEntity<SnippetResponse> deleteSnippet(@PathVariable Long id) {
        snippetService.deleteSnippet(id);
        return ResponseEntity.noContent().build();
    }

    // search by language
    @GetMapping("/language")
    public ResponseEntity<List<SnippetResponse>> getByLanguages(@RequestParam String languages) {
        return ResponseEntity.ok(snippetService.getByLanguage(languages));


    }

    /*
    @RequestParam

            Value comes after a ? in the URL. Used for filters and search terms.
            Example: /api/snippets/search?keyword=hello — hello is the param.
    */

    //search by title keyword

    @GetMapping("/title")
    public ResponseEntity<List<SnippetResponse>> getBytitle(@RequestParam String keyword) {
        return ResponseEntity.ok(snippetService.getByTitle(keyword));
    }

    // search by full text all fields

    @GetMapping("/search")
    public ResponseEntity<Page<SnippetResponse>> search(@RequestParam String keyword,@PageableDefault(
            size=10,
            sort="createdAt",
            direction = Sort.Direction.DESC

    )Pageable pageable) {
        return ResponseEntity.ok(snippetService.search(keyword,pageable));
        // pageable make the sql automatically include LIMIT and OFFET

    }
}
    /*
    ResponseEntity
            A wrapper that lets you control the full HTTP response — body + status code.
             ResponseEntity.ok() = 200, .status(CREATED) = 201, .noContent() = 204.
     */
/*
Paging

1. The Core Components

        Pageable (The Request):
        An interface that holds the instructions from the user
        (e.g., "Give me page 2, 10 items, sorted by date").

        Page<T> (The Response):
        A wrapper that contains your data (List<T>) plus metadata like
        totalPages, totalElements, and isLast.

        PagingAndSortingRepository:
        The internal Spring interface that provides the findAll(Pageable) method.


2. Key Annotations

        @PageableDefault
        Placed on the Controller parameter to set "fallback" values
        if the user doesn't provide ?page or ?size in the URL.

        @EnableSpringDataWebSupport
        Located in WebConfig. It enables the automatic translation of
        URL parameters into a Pageable object.

        @Configuration
        Marks the WebConfig class as a source of bean definitions
        and settings for the Spring context.


3. Essential Method Syntax

        In Repository:
        You don't need to add anything; JpaRepository already includes pagination support.

        In Service:
        repository.findAll(pageable) — Returns a Page<Entity>.
        .map(this::mapToResponse) — Converts that Page of Entities into a Page of DTOs.

        In Controller:
        public ResponseEntity<Page<DTO>> getAll(@PageableDefault Pageable pageable)
        — Captures the request and returns the paginated response.


4. URL Parameter Cheat Sheet

        ?page=0
        The page index (0-based).

        ?size=5
        Number of items per page.

        ?sort=fieldName,asc|desc
        The field to sort by and the direction.

        Combined:
        ?page=0&size=10&sort=createdAt,desc
*/
