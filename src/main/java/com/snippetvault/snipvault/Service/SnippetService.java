package com.snippetvault.snipvault.Service;

import com.snippetvault.snipvault.DTO.SnippetRequest;
import com.snippetvault.snipvault.DTO.SnippetResponse;
import com.snippetvault.snipvault.Exception.ResourceNotFound;
import com.snippetvault.snipvault.repository.SnippetRepository;


import com.snippetvault.snipvault.model.snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.snippetvault.snipvault.DTO.SnippetActivityEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service                            // this is a business logic layer. Create a Bean for this (same as @Componenet)
@RequiredArgsConstructor            // Create constructor for repo and injects its bean here
public class SnippetService {
    private final SnippetRepository snippetRepository;
    private final SimpMessagingTemplate messagingTemplate;
// @RequiredArgsConstructor will auto-inject it

    //getAll
    @Cacheable(value = "snippets", key = "'all::' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<SnippetResponse> getAllSnippets(Pageable pageable) {

        return snippetRepository.findAll(pageable)
                .map(this::toResponse);

    }

    //getById
    @Cacheable(value = "snippets", key = "'id::' + #id")
    public SnippetResponse getSnippetById(Long id) {
        snippet snippetobj=snippetRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Snippet not found with id: " + id));
        return toResponse(snippetobj);
    }

    //Create
    @CacheEvict(value = "snippets", allEntries = true)
    public SnippetResponse createSnippet(SnippetRequest request,String username) {
        snippet snippetobj=toEntity(request);
        snippet saved=snippetRepository.save(snippetobj);
        messagingTemplate.convertAndSend(
                "/topic/activity",
                new SnippetActivityEvent(
                        "CREATED",
                        username,
                        saved.getTitle(),
                        saved.getLanguage(),
                        LocalDateTime.now()
                )
        );
        return toResponse(saved);
    }

    // Update
    @CacheEvict(value = "snippets", allEntries = true)
    public SnippetResponse updateSnippet(Long id,SnippetRequest request,String username) {

        snippet snippetobj=snippetRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Snippet not found with id: " + id));
        snippetobj.setDescription(request.getDescription());
        snippetobj.setCode(request.getCode());
        snippetobj.setLanguage(request.getLanguage());
        snippetobj.setTitle(request.getTitle());
        snippet updated=snippetRepository.save(snippetobj);
        messagingTemplate.convertAndSend(
                "/topic/activity",
                new SnippetActivityEvent(
                        "UPDATED",
                        username,
                        updated.getTitle(),
                        updated.getLanguage(),
                        LocalDateTime.now()
                )
        );
        return toResponse(updated);
    }

    //delete
    @CacheEvict(value = "snippets", allEntries = true)
    public void deleteSnippet(Long id,String username) {
        if(!snippetRepository.existsById(id)){
            throw new ResourceNotFound("Snippet not found with id: " + id);
        }
        snippet snippetobj=snippetRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Snippet not found with id: " + id));
        messagingTemplate.convertAndSend(
                "/topic/activity",
                new SnippetActivityEvent(
                        "DELETED",
                        username,
                        snippetobj.getTitle(),
                        snippetobj.getLanguage(),
                        LocalDateTime.now()
                )
        );
        snippetRepository.deleteById(id);
    }

    //search by language
    @Cacheable(value = "snippets", key = "'lang::' + #language")
    public List<SnippetResponse> getByLanguage(String language){
        return snippetRepository.findByLanguageIgnoreCase(language)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

//    //search by keyword title
//    public List<SnippetResponse> getByTitle(String keyword){
//        return snippetRepository.findByTitleContainingIgnoreCase(keyword)
//                .stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//
//    }
    // search by full text all fields
    @Cacheable(value = "snippets", key = "'search::' + #keyword + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<SnippetResponse> search(String keyword, Pageable pageable) {

        if(keyword==null || keyword.isEmpty()){
            throw new IllegalArgumentException("Search Keyword cannot be blank");
        }
        return snippetRepository.searchByKeyword(keyword,pageable)
                .map(this::toResponse);
    }

    // - Mappers - //

    private snippet toEntity(SnippetRequest request) {
        snippet s=new snippet();
        s.setDescription(request.getDescription());
        s.setCode(request.getCode());
        s.setLanguage(request.getLanguage());
        s.setTitle(request.getTitle());
        return s;
    }

    private SnippetResponse toResponse(snippet s) {
        SnippetResponse res=new SnippetResponse();
        res.setId(s.getId());
        res.setTitle(s.getTitle());
        res.setLanguage(s.getLanguage());
        res.setDescription(s.getDescription());
        res.setCreatedAt(s.getCreatedAt());
        res.setCode(s.getCode());
        return res;


    }



}
