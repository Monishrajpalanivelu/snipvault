package com.snippetvault.snipvault.Controller;

import com.snippetvault.snipvault.DTO.SnippetRequest;
import com.snippetvault.snipvault.DTO.SnippetResponse;
import com.snippetvault.snipvault.Service.SnippetService;
import com.snippetvault.snipvault.repository.SnippetRepository;
import jakarta.validation.Valid;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<SnippetResponse>> getAllSnippets() {
            return ResponseEntity.ok(snippetService.getAllSnippets());
    }

    // get snippet by id
    @GetMapping("/{id}")
    public ResponseEntity<SnippetResponse> getSnippetById(@PathVariable Long id) {
        return  ResponseEntity.ok(snippetService.getSnippetById(id));
    }

    // create new snippet
    @PostMapping
    public ResponseEntity<SnippetResponse> createSnippet(@Valid @RequestBody SnippetRequest request) {
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(snippetService.createSnippet(request));

    }

    //update a snippet
    //@PathVariable translate the String id in the url to long id/datatype any id
    //@requestbody Tells Spring to read the JSON from the request body and deserialise(json ->java objects) it into a SnippetRequest object automatically.
    @PutMapping("{id}")
    public ResponseEntity<SnippetResponse> updateSnippet(@PathVariable Long id,@Valid @RequestBody SnippetRequest request) {

        return   ResponseEntity.ok(snippetService.updateSnippet(id, request));

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

    @GetMapping("/search")
    public ResponseEntity<List<SnippetResponse>> getBytitle(@RequestParam String keyword) {
        return ResponseEntity.ok(snippetService.getByTitle(keyword));
    }

    /*
    ResponseEntity
            A wrapper that lets you control the full HTTP response — body + status code.
             ResponseEntity.ok() = 200, .status(CREATED) = 201, .noContent() = 204.
     */
}
